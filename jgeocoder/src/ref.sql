SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO



CREATE FUNCTION dbo.fnGeoFromHit
    (
        @Number int,
        @TLID varchar(50),
        @FromAddR int, 
        @FromAddL int, 
        @ToAddR int,
        @ToAddL int, 
        @zipL int, 
        @zipR int,
        @FromLng decimal(9,6),
        @FromLat decimal(9,6),
         @Long1 decimal(9,6),
        @Lat1 decimal(9,6),
        @Long2 decimal(9,6),
        @Lat2 decimal(9,6),
        @Long3 decimal(9,6),
        @Lat3 decimal(9,6),
        @Long4 decimal(9,6),
        @Lat4 decimal(9,6),
        @Long5 decimal(9,6),
        @Lat5 decimal(9,6),
        @Long6 decimal(9,6),
        @Lat6 decimal(9,6),
        @Long7 decimal(9,6),
        @Lat7 decimal(9,6),
        @Long8 decimal(9,6),
        @Lat8 decimal(9,6),
        @Long9 decimal(9,6),
        @Lat9 decimal(9,6),
        @Long10 decimal(9,6),
        @Lat10 decimal(9,6),
        @ToLng decimal(9,6),
        @ToLat decimal(9,6)
    )
RETURNS  @Location TABLE 
    (
    Longitude Decimal(9,6), 
    Latitude Decimal(9,6),
    ZIP int,
    TLID varchar(50)
    )
AS
    BEGIN
    
    declare @latTotal decimal(9,6) 
    declare @lngTotal decimal(9,6) 
    set @latTotal = 0
    set @lngTotal = 0

    
    select @lngTotal = TotalLong, @latTotal = TotalLat from 
    fnTotalChainDistance(@FromLng, @FromLat, @Long1,@Lat1,@Long2,@Lat2,@Long3,@Lat3,@Long4,@Lat4,@Long5,@Lat5,@Long6,@Lat6,@Long7,@Lat7,@Long8,@Lat8,@Long9,@Lat9,@Long10,@Lat10, @Tolng,@ToLat)


    
    declare @rel decimal(9,6)
    
    declare @dec1 decimal(20,6)
    declare @dec2 decimal(20,6)
    
    --which side of the street do we use?
    declare @AddStart int
    declare @AddEnd int
    declare @usezip int

    declare @useLeft int
    declare @useRight int
    set @useLeft = 0
    set @useRight = 0
    
    --we need to throw out sides if they are not in range or were null in the db (-1)
    if @FromAddR = -1 OR ((@Number not between @fromaddr and @toaddr) and (@Number not between @toaddr and @fromaddr))
    begin
        set @useRight = -1
        set @useLeft = 1
    end
    
    if @FromAddL = -1 OR ((@Number not between @fromaddL and @toaddL) and (@Number not between @toaddL and @fromaddL))
    begin
        set @useLeft = -1
        set @useRight = 1
    end

    --make sure the number was not null, the parity matches, and that its in range
    if @useRight <> -1 and ((@FromAddR % 2) = (@Number % 2) OR @useLeft = -1)
    begin
        --use the right side
        set @AddStart = @FromAddr
        set @AddEnd = @ToAddr
        set @usezip = @zipr
    end
    else
    begin
        --use the left side
        set @AddStart = @FromAddL
        set @AddEnd = @ToAddL
        set @usezip = @zipL
    
    end



    set @dec1 = @AddEnd - @Number
    set @dec2 = @AddEnd - @AddStart



    set @rel = (@dec1 / @dec2)
    if @rel = 1
    begin
        --return start
        INSERT into @Location 
        SELECT  @FromLng,@FromLat,@usezip,@TLID
        return
    end
    
    if @rel = 0
    begin
        --return end
        INSERT into @Location 
        SELECT  @ToLng,@ToLat,@usezip,@TLID
        return
    end
    
    
    declare @lat decimal(9,6)
    declare @lon decimal(9,6)
    
    --This is the straight line calculation, leaving it in for testing
    set @lat = @FromLat + (@rel * @latTotal)
    set @lon = @FromLng + (@rel * @lngTotal)
    

    ----End point of entire chain if this was a straight line so we can calculate total distance
    declare @tempEndLat decimal(9,6)
    declare @tempEndLng decimal(9,6)
    set @tempEndLat = @fromLat + @latTotal
    set @tempEndLng = @fromLng + @lngTotal

    declare @totalDist decimal(20,6)
    set @totalDist = dbo.fnLineDistance(@fromLng, @fromLat,@tempEndLng, @tempEndLat)

    --Now we need to go back through the chain
    --We have the percentage we need to travel (@rel) and the total distance (@totalDist)    
    --Follow the chain until we find our point    
    
    select @lon = Longitude, @lat = Latitude from 
    dbo.fnPointFromChainRatio(@totalDist, @rel, @FromLng, @FromLat, @Long1,@Lat1,@Long2,@Lat2,@Long3,@Lat3,@Long4,@Lat4,@Long5,@Lat5,@Long6,@Lat6,@Long7,@Lat7,@Long8,@Lat8,@Long9,@Lat9,@Long10,@Lat10, @Tolng,@ToLat)

    Insert into @Location
        Select @lon,@lat,@usezip,@TLID

    RETURN
    END



GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO





CREATE   FUNCTION dbo.fnGeoQueryCounty
    (@DirPrefix varchar(2) = '', @Number int, @StreetName varchar(50), @StreetType varchar(50), @DirSuffix varchar(2) = '', @FipsStateCode int, @FipsCountyCode int)

RETURNS @GEO TABLE 
(
FromAddR int,
FromAddL int,
ToAddR int,
ToAddL int,
zipL int,
zipR int,
ToLat decimal(9,6),
ToLng decimal(9,6),
FromLng decimal(9,6),
FromLat decimal(9,6),
Long1 decimal(9,6),
Lat1 decimal(9,6),
Long2 decimal(9,6),
Lat2 decimal(9,6),
Long3 decimal(9,6),
Lat3 decimal(9,6),
Long4 decimal(9,6),
Lat4 decimal(9,6),
Long5 decimal(9,6),
Lat5 decimal(9,6),
Long6 decimal(9,6),
Lat6 decimal(9,6),
Long7 decimal(9,6),
Lat7 decimal(9,6),
Long8 decimal(9,6),
Lat8 decimal(9,6),
Long9 decimal(9,6),
Lat9 decimal(9,6),
Long10 decimal(9,6),
Lat10 decimal(9,6),
TLID varchar(50)
)
AS
    BEGIN
    
    declare @todecimal decimal(9,6)
    set @todecimal = 0.000001

INSERT INTO @GEO
select top 1 
    isnull(t1.fraddr,-1) , isnull(t1.fraddl,-1) , isnull(t1.toaddr,-1), isnull(t1.toaddl,-1), t1.zipl, t1.zipr, t1.tolat * @todecimal, t1.tolong * @todecimal, t1.frlong * @todecimal, t1.frlat * @todecimal, 
        isnull(t2.long1,0)  * @todecimal, isnull(t2.lat1,0) * @todecimal, isnull(t2.long2,0) * @todecimal, isnull(t2.lat2,0) * @todecimal, isnull(t2.long3,0) * @todecimal, isnull(t2.lat3,0) * @todecimal, isnull(t2.long4,0) * @todecimal, isnull(t2.lat4,0) * @todecimal, isnull(t2.long5,0) * @todecimal, isnull(t2.lat5,0) * @todecimal, isnull(t2.long6,0) * @todecimal, isnull(t2.lat6,0) * @todecimal, isnull(t2.long7,0) * @todecimal, isnull(t2.lat7,0) * @todecimal, isnull(t2.long8,0) * @todecimal, isnull(t2.lat8,0) * @todecimal, isnull(t2.long9,0) * @todecimal, isnull(t2.lat9,0) * @todecimal, isnull(t2.long10,0) * @todecimal, isnull(t2.lat10,0) * @todecimal, t1.TLID
      from TIGER_01 t1 with (nolock) left outer join TIGER_02 t2  with (nolock) on t1.tlid = t2.tlid  where fename = @StreetName and fetype = @StreetType and fedirp = @DirPrefix and fedirs = @DirSuffix and (statel = @FipsStateCode or stater =@FipsStateCode) and (countyl = @FipsCountyCode or countyr =@FipsCountyCode)
       and ((fraddl <= @Number and toaddl >= @Number) or (fraddl >= @Number and toaddl <= @Number )
        OR (fraddr <= @Number and toaddr >= @Number) OR (fraddr >= @Number and toaddr <= @Number) )

    RETURN
    END





GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO





CREATE   FUNCTION dbo.fnGeoQueryState
    (@DirPrefix varchar(2) = '', @Number int, @StreetName varchar(50), @StreetType varchar(50), @DirSuffix varchar(2) ='', @FipsStateCode int)

RETURNS @GEO TABLE 
(
FromAddR int,
FromAddL int,
ToAddR int,
ToAddL int,
zipL int,
zipR int,
ToLat decimal(9,6),
ToLng decimal(9,6),
FromLng decimal(9,6),
FromLat decimal(9,6),

Long1 decimal(9,6),
Lat1 decimal(9,6),
Long2 decimal(9,6),
Lat2 decimal(9,6),
Long3 decimal(9,6),
Lat3 decimal(9,6),
Long4 decimal(9,6),
Lat4 decimal(9,6),
Long5 decimal(9,6),
Lat5 decimal(9,6),
Long6 decimal(9,6),
Lat6 decimal(9,6),
Long7 decimal(9,6),
Lat7 decimal(9,6),
Long8 decimal(9,6),
Lat8 decimal(9,6),
Long9 decimal(9,6),
Lat9 decimal(9,6),
Long10 decimal(9,6),
Lat10 decimal(9,6),
TLID varchar(50)
)
AS
    BEGIN
    
    declare @todecimal decimal(9,6)
    set @todecimal = 0.000001

INSERT INTO @GEO
select top 1 
    isnull(t1.fraddr,-1) , isnull(t1.fraddl,-1) , isnull(t1.toaddr,-1), isnull(t1.toaddl,-1), t1.zipl, t1.zipr, t1.tolat * @todecimal, t1.tolong * @todecimal, t1.frlong * @todecimal, t1.frlat * @todecimal, 
        isnull(t2.long1,0)  * @todecimal, isnull(t2.lat1,0) * @todecimal, isnull(t2.long2,0) * @todecimal, isnull(t2.lat2,0) * @todecimal, isnull(t2.long3,0) * @todecimal, isnull(t2.lat3,0) * @todecimal, isnull(t2.long4,0) * @todecimal, isnull(t2.lat4,0) * @todecimal, isnull(t2.long5,0) * @todecimal, isnull(t2.lat5,0) * @todecimal, isnull(t2.long6,0) * @todecimal, isnull(t2.lat6,0) * @todecimal, isnull(t2.long7,0) * @todecimal, isnull(t2.lat7,0) * @todecimal, isnull(t2.long8,0) * @todecimal, isnull(t2.lat8,0) * @todecimal, isnull(t2.long9,0) * @todecimal, isnull(t2.lat9,0) * @todecimal, isnull(t2.long10,0) * @todecimal, isnull(t2.lat10,0) * @todecimal, t1.TLID
      from TIGER_01 t1 with (nolock) left outer join TIGER_02 t2  with (nolock) on t1.tlid = t2.tlid  where fename = @StreetName and fetype = @StreetType and fedirp = @DirPrefix and fedirs = @DirSuffix and (statel = @FipsStateCode or stater =@FipsStateCode)
       and ((fraddl <= @Number and toaddl >= @Number) or (fraddl >= @Number and toaddl <= @Number )
        OR (fraddr <= @Number and toaddr >= @Number) OR (fraddr >= @Number and toaddr <= @Number) )

    RETURN
    END





GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO





CREATE   FUNCTION dbo.fnGeoQueryZIP
    (@DirPrefix varchar(2) = '', @Number int, @StreetName varchar(50), @StreetType varchar(50), @DirSuffix varchar(2) ='', @ZipCode int)

RETURNS @GEO TABLE 
(
FromAddR int,
FromAddL int,
ToAddR int,
ToAddL int,
zipL int,
zipR int,
ToLat decimal(9,6),
ToLng decimal(9,6),
FromLng decimal(9,6),
FromLat decimal(9,6),

Long1 decimal(9,6),
Lat1 decimal(9,6),
Long2 decimal(9,6),
Lat2 decimal(9,6),
Long3 decimal(9,6),
Lat3 decimal(9,6),
Long4 decimal(9,6),
Lat4 decimal(9,6),
Long5 decimal(9,6),
Lat5 decimal(9,6),
Long6 decimal(9,6),
Lat6 decimal(9,6),
Long7 decimal(9,6),
Lat7 decimal(9,6),
Long8 decimal(9,6),
Lat8 decimal(9,6),
Long9 decimal(9,6),
Lat9 decimal(9,6),
Long10 decimal(9,6),
Lat10 decimal(9,6),
TLID varchar(50)
)
AS
    BEGIN
    
    declare @todecimal decimal(9,6)
    set @todecimal = 0.000001

INSERT INTO @GEO
select top 1 
    isnull(t1.fraddr,-1) , isnull(t1.fraddl,-1) , isnull(t1.toaddr,-1), isnull(t1.toaddl,-1), t1.zipl, t1.zipr, t1.tolat * @todecimal, t1.tolong * @todecimal, t1.frlong * @todecimal, t1.frlat * @todecimal, 
        isnull(t2.long1,0)  * @todecimal, isnull(t2.lat1,0) * @todecimal, isnull(t2.long2,0) * @todecimal, isnull(t2.lat2,0) * @todecimal, isnull(t2.long3,0) * @todecimal, isnull(t2.lat3,0) * @todecimal, isnull(t2.long4,0) * @todecimal, isnull(t2.lat4,0) * @todecimal, isnull(t2.long5,0) * @todecimal, isnull(t2.lat5,0) * @todecimal, isnull(t2.long6,0) * @todecimal, isnull(t2.lat6,0) * @todecimal, isnull(t2.long7,0) * @todecimal, isnull(t2.lat7,0) * @todecimal, isnull(t2.long8,0) * @todecimal, isnull(t2.lat8,0) * @todecimal, isnull(t2.long9,0) * @todecimal, isnull(t2.lat9,0) * @todecimal, isnull(t2.long10,0) * @todecimal, isnull(t2.lat10,0) * @todecimal, t1.TLID
      from TIGER_01 t1 with (nolock) left outer join TIGER_02 t2  with (nolock) on t1.tlid = t2.tlid  where fename = @StreetName and fetype = @StreetType and fedirp = @DirPrefix and fedirs = @DirSuffix and (zipl = @ZipCode or zipr =@ZipCode)
       and ((fraddl <= @Number and toaddl >= @Number) or (fraddl >= @Number and toaddl <= @Number )
        OR (fraddr <= @Number and toaddr >= @Number) OR (fraddr >= @Number and toaddr <= @Number) )

    RETURN
    END





GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO








/*
© 2005 John Sample

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

*/


CREATE    FUNCTION dbo.fnGeocode (@DirPrefix varchar(2) = '', @Number int, @StreetName varchar(50), @StreetType varchar(50), @DirSuffix varchar(2) = '', @ZipCode int =-1 , @FipsStateCode int=-1, @FipsCountyCode int =-1)
RETURNS @Location TABLE 
    (
    Longitude Decimal(9,6), 
    Latitude Decimal(9,6),
    ZIP int,
    TLID varchar(50)
    )
AS
BEGIN


declare @FromLat decimal(9,6)
declare @ToLat decimal(9,6)
declare @FromLng decimal(9,6)
declare @ToLng decimal(9,6)

declare @TLID varchar(50)

declare @FromAddR int
declare @ToAddR int
declare @FromAddL int
declare @ToAddL int

declare @zipL int
declare @zipR int

--we need 10 lat/long pairs for type 2 records
declare @Long1 decimal(9,6)
declare @Lat1 decimal(9,6)
declare @Long2 decimal(9,6)
declare @Lat2 decimal(9,6)
declare @Long3 decimal(9,6)
declare @Lat3 decimal(9,6)
declare @Long4 decimal(9,6)
declare @Lat4 decimal(9,6)
declare @Long5 decimal(9,6)
declare @Lat5 decimal(9,6)
declare @Long6 decimal(9,6)
declare @Lat6 decimal(9,6)
declare @Long7 decimal(9,6)
declare @Lat7 decimal(9,6)
declare @Long8 decimal(9,6)
declare @Lat8 decimal(9,6)
declare @Long9 decimal(9,6)
declare @Lat9 decimal(9,6)
declare @Long10 decimal(9,6)
declare @Lat10 decimal(9,6)

declare @querypicked int
set @querypicked = 0

declare @todecimal decimal(9,6)
set @todecimal = 0.000001

-----------------------------------------------------------------------
---which query do we use?----------------------------------------------
/*
This section is responsible for finding a suitable tiger record.
If a hit is found its passed off to the geocoding function
*/
--zip query
if (@ZipCode <> -1 and @querypicked = 0)
begin
    set @querypicked = 1
    
    DECLARE c1 CURSOR FOR
        select * from fnGeoQueryZIP(@DirPrefix, @Number, @StreetName,@StreetType, @DirSuffix, @ZipCode)

end

--county query
if (@FipsStateCode <> -1 and @FipsCountyCode <> -1 and @querypicked = 0)
begin
    set @querypicked = 1
    DECLARE c1 CURSOR FOR
    select * from fnGeoQueryCounty(@DirPrefix, @Number, @StreetName,@StreetType, @DirSuffix, @FipsStateCode, @FipsCountyCode)

end

--state query
if (@FipsStateCode <> -1 and @querypicked = 0)
begin
    set @querypicked = 1
    DECLARE c1 CURSOR FOR
    select * from fnGeoQueryState(@DirPrefix, @Number, @StreetName,@StreetType, @DirSuffix, @FipsStateCode)
end
-----------------------------------------------------------------------


OPEN c1

FETCH NEXT FROM c1 INTO 
    @FromAddR, @FromAddL, @ToAddR, @ToAddL, @zipL, @zipR, @ToLat, @ToLng, @FromLng, @FromLat,@Long1, @Lat1,@Long2, @Lat2,@Long3, @Lat3,@Long4, @Lat4,@Long5, @Lat5,@Long6, @Lat6,@Long7, @Lat7,@Long8, @Lat8,@Long9, @Lat9, @Long10, @Lat10, @TLID

if (@@FETCH_STATUS = 0)
BEGIN
    
    /*
        We've got a hit. pass the info off to the geocoding function. We should be guaranteed a lng/lat.
    */

    insert into @Location
        select * from fnGeoFromHit(@Number,@TLID,@FromAddR,@FromAddl,@ToAddr,@ToAddL,@ZipL,@ZipR,@FromLng,@FromLat,@Long1,@Lat1,@Long2,@Lat2,@Long3,@Lat3,@Long4,@Lat4,@Long5,@Lat5,@Long6,@Lat6,@Long7,@Lat7,@Long8,@Lat8,@Long9,@Lat9,@Long10,@Lat10, @Tolng,@ToLat)
    
END


CLOSE c1
DEALLOCATE c1
RETURN 
    
END












GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO






CREATE    function dbo.fnGeocodeClosestAddress
(@DirPrefix varchar(2) = '', @Number int, @StreetName varchar(50), @StreetType varchar(50), @DirSuffix varchar(2) = '', @ZipCode int)
RETURNS @Location TABLE 
    (
    Longitude Decimal(9,6), 
    Latitude Decimal(9,6),
    ZIP int,
    TLID varchar(50)
    )
as
begin
declare @todecimal decimal(9,6)
set @todecimal = 0.000001

insert into @Location
select  frlong * @todecimal, frlat * @todecimal, isnull(zipl,zipr),tlid from tiger_01 t1 where 
t1.fename = @streetname and t1.fetype =  @streettype and t1.fedirp = @DirPrefix and fedirs = @DirSuffix and (zipl = @ZipCode or zipr =@ZipCode)
and abs(((isnull(fraddr,fraddl) + isnull(fraddl,fraddr))/2) - @number) = (select min(abs(((isnull(fraddr,fraddl) + isnull(fraddl,fraddr))/2) - @number)) from tiger_01 t1 where fename = @streetname and fetype = @streettype  and t1.fedirp = @DirPrefix and fedirs = @DirSuffix and (zipl = @ZipCode or zipr =@ZipCode))

--We're only checking the closest start address record
--to be more accurate, we should test the end addresses also, then return the long/lat thats closest
/*
UNION
select  abs(fraddl - @number), tlid, fename, fetype, fraddr, toaddr, fraddl, toaddl from tiger_01 t1 where 
t1.fename = @streetname and t1.fetype =  @streettype
and abs(((isnull(toaddr,toaddl) + isnull(toaddl,toaddr))/2) - @number) = (select min(abs(((isnull(toaddr,toaddl) + isnull(toaddl,toaddr))/2) - @number)) from tiger_01 t1 where fename = @streetname and fetype = @streettype )
*/
return
end







GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO





/*
© 2005 John Sample

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

*/
/*
If roads a and b intersect, we should find one of the following:
a.frlat & a.frlong = b.frlat & b.frlong
or
a.frlat & a.frlong = b.tolat & b.tolong
or
a.tolat & a.tolong = b.frlat b a.frlong
or
a.tolat & a.tolong = b.tolat & b.tolong

*/

CREATE   FUNCTION dbo.fnGeocodeIntersection
(
@DirPrefixA varchar(2) = '',  @StreetNameA varchar(50), @StreetTypeA varchar(50), @DirSuffixA varchar(2) ='', @ZipA int,
@DirPrefixB varchar(2) = '',  @StreetNameB varchar(50), @StreetTypeB varchar(50), @DirSuffixB varchar(2) ='', @ZipB int
)
RETURNS @Location TABLE 
    (
    Longitude Decimal(9,6), 
    Latitude Decimal(9,6)
    )
AS
BEGIN

declare @todecimal decimal(9,6)
set @todecimal = 0.000001

INSERT into @Location 
select frlong * @todecimal as Longitude, frlat * @todecimal  as Latitude from tiger_01 t1a where t1a.fename = @StreetNameA and t1a.fetype = @StreetTypeA and t1a.fedirp = @DirPrefixA and t1a.fedirs = @DirSuffixA and (t1a.zipl = @ZipA or t1a.zipr =@ZipA)
and 
(
exists(select 1 from tiger_01 t1b where t1b.fename = @StreetNameB and t1b.fetype = @StreetTypeB and (t1b.zipl = @ZipB or t1b.zipr =@ZipB) and t1b.fedirp = @DirPrefixB and t1b.fedirs = @DirSuffixB and t1b.frlat= t1a.frlat and t1b.frlong= t1a.frlong)
or 
exists(select 1 from tiger_01 t1b where t1b.fename = @StreetNameB and t1b.fetype = @StreetTypeB and (t1b.zipl = @ZipB or t1b.zipr =@ZipB) and t1b.fedirp = @DirPrefixB and t1b.fedirs = @DirSuffixB and t1b.tolat= t1a.frlat and t1b.tolong= t1a.frlong)
)
UNION
select tolong * @todecimal as Longitude, tolat * @todecimal as Latitude from tiger_01 t1a where t1a.fename = @StreetNameA and t1a.fetype = @StreetTypeA and t1a.fedirp = @DirPrefixA and t1a.fedirs = @DirSuffixA and (t1a.zipl = @ZipA or t1a.zipr =@ZipA)
and 
(
exists(select 1 from tiger_01 t1b where t1b.fename = @StreetNameB and t1b.fetype = @StreetTypeB and (t1b.zipl = @ZipB or t1b.zipr =@ZipB) and t1b.fedirp = @DirPrefixB and t1b.fedirs = @DirSuffixB and t1b.frlat= t1a.tolat and t1b.frlong= t1a.tolong)
or 
exists(select 1 from tiger_01 t1b where t1b.fename = @StreetNameB and t1b.fetype = @StreetTypeB and (t1b.zipl = @ZipB or t1b.zipr =@ZipB) and t1b.fedirp = @DirPrefixB and t1b.fedirs = @DirSuffixB and t1b.tolat= t1a.tolat and t1b.tolong= t1a.tolong)
)


return
END







GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO





CREATE  FUNCTION dbo.fnLineDistance
    (
    
    @x1 decimal(9,6),
    @y1 decimal(9,6),
    @x2 decimal(9,6),
    @y2 decimal(9,6)
    )
RETURNS decimal(20,6)
AS
    BEGIN
        


    declare @dx decimal(9,6)
    declare @dy decimal(9,6)
    declare @temp decimal(20,6)
        
        set @dx = @x2 - @x1
        set @dy = @y2 - @y1
     set @temp = sqrt((@dx*@dx) + (@dy*@dy))
    RETURN @temp

    END





GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO



CREATE FUNCTION dbo.fnLineRatio
    (
    
    @TotalLength decimal(9,6),
    @SegmentLength decimal(9,6)
    
    )
RETURNS decimal(9,6)
AS
    BEGIN
        
    RETURN @SegmentLength / @TotalLength
    
    END



GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO






CREATE  FUNCTION dbo.fnPointDistanceFromLine
    (
    
    @x1 decimal(9,6),
    @y1 decimal(9,6),
    @x2 decimal(9,6),
    @y2 decimal(9,6),
    @PointX decimal(9,6),
    @PointY decimal(9,6)
    )
RETURNS decimal(20,6)
AS
    BEGIN
        


    declare @dx decimal(9,6)
    declare @dy decimal(9,6)
    declare @temp decimal(20,6)
    declare @distance decimal(9,6)    

        set @dx = @x2 - @x1
        set @dy = @y2 - @y1
     set @temp = sqrt((@dx*@dx) + (@dy*@dy))
    
    set @distance =  (@dx*(@PointY - @y1) - @dy*(@PointX - @x1))/@temp

    RETURN @distance

    END






GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO




/*
© 2005 John Sample

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

*/
CREATE FUNCTION dbo.fnPointFromChainRatio
    (
        @TotalLength decimal(9,6),
        @Ratio decimal(9,6),
        @FromLng decimal(9,6),
        @FromLat decimal(9,6),
         @Long1 decimal(9,6),
        @Lat1 decimal(9,6),
        @Long2 decimal(9,6),
        @Lat2 decimal(9,6),
        @Long3 decimal(9,6),
        @Lat3 decimal(9,6),
        @Long4 decimal(9,6),
        @Lat4 decimal(9,6),
        @Long5 decimal(9,6),
        @Lat5 decimal(9,6),
        @Long6 decimal(9,6),
        @Lat6 decimal(9,6),
        @Long7 decimal(9,6),
        @Lat7 decimal(9,6),
        @Long8 decimal(9,6),
        @Lat8 decimal(9,6),
        @Long9 decimal(9,6),
        @Lat9 decimal(9,6),
        @Long10 decimal(9,6),
        @Lat10 decimal(9,6),
        @ToLng decimal(9,6),
        @ToLat decimal(9,6)
    )

RETURNS @LongLat  TABLE
    (
    Longitude decimal(9,6), 
    Latitude decimal(9,6)
    )
AS
BEGIN
    
    --buffer for last x,y coords
    declare @lastLng decimal(9,6)
    declare @lastLat decimal(9,6)

    set @lastLng = @fromLng
    set @lastLat = @fromLat    

    declare @found int
    set @found = 0

    declare @totalRatio decimal(9,6)--Tracks the total percentage of the line 
    declare @totalTravel decimal(9,6)--Tracks the total distance we have travelled
    declare @travelTarget decimal(9,6)
    set @totalRatio = 0.0
    set @totalTravel = 0.0
    set @travelTarget = @ratio * @TotalLength

    ---For each lng/lat pair that isn't empty, calculate distance from last non empty pair.
    ---Is the totalRatio + ratio of this segment < the ratio we are looking for?
    ---If yes, add the ratio of this distance to the total ratio
    ---If no, trim the ratio so that it applies only to this segment and return calculated point.

    --buffer vars to keep the code a bit shorter
    declare @thisLen decimal(9,6)
    declare @thisRatio decimal(20,6)

    declare @useStartLong decimal(9,6)
    declare @useStartLat decimal(9,6)
    declare @useEndLong decimal(9,6)
    declare @useEndLat decimal(9,6)

    declare @useRatio decimal(9,6)
    
    
    --we've got to do this 10 times
    if ((@Lat1 <> 0) and (@found = 0) )
    begin
        set @thisLen = dbo.fnLineDistance(@long1, @lat1, @lastLng, @lastLat)
        set @thisRatio = @thisLen / @TotalLength

        if (@thisLen + @totalTravel >= @travelTarget)
        begin
          set @useStartLong = @lastLng
          set @useStartLat = @lastLat
          set @useEndLong  = @Long1
          set @useEndLat  = @Lat1
          
          set @useRatio = @thisRatio
          set @found = 1
        end
        else
        begin
         set @totalRatio = @totalRatio + @thisRatio
         set @lastLng = @Long1
         set @lastLat = @Lat1
         set @totalTravel = @totalTravel + @thisLen
        end
    end



    if (@Lat2 <> 0 and @found = 0) 
    begin        
        set @thisLen = dbo.fnLineDistance(@long2, @lat2, @lastLng, @lastLat)
        set @thisRatio = @thisLen / @TotalLength

        if (@thisLen + @totalTravel >= @travelTarget)
        begin
          set @useStartLong  = @lastLng
          set @useStartLat  = @lastLat
          set @useEndLong = @Long2
          set @useEndLat  = @Lat2
          
          set @useRatio = @thisRatio
          set @found = 1
        end
        else
        begin
         set @totalRatio = @totalRatio + @thisRatio
         set @lastLng = @Long2
         set @lastLat = @Lat2
         set @totalTravel = @totalTravel + @thisLen
        end
    end



    if (@Lat3 <> 0 and @found = 0) 
    begin
        set @thisLen = dbo.fnLineDistance(@long3, @lat3, @lastLng, @lastLat)
        set @thisRatio = @thisLen / @TotalLength

        if (@thisLen + @totalTravel >= @travelTarget)
        begin
          set @useStartLong  = @lastLng
          set @useStartLat  = @lastLat
          set @useEndLong  = @Long3
          set @useEndLat = @Lat3
          
          set @useRatio = @thisRatio
          set @found = 1
        end
        else
        begin
         set @totalRatio = @totalRatio + @thisRatio
         set @lastLng = @Long3
         set @lastLat = @Lat3
         set @totalTravel = @totalTravel + @thisLen
        end    
    end



    if (@Lat4 <> 0 and @found = 0) 
    begin
        set @thisLen = dbo.fnLineDistance(@long4, @lat4, @lastLng, @lastLat)
        set @thisRatio = @thisLen / @TotalLength

        if (@thisLen + @totalTravel >= @travelTarget)
        begin
          set @useStartLong  = @lastLng
          set @useStartLat  = @lastLat
          set @useEndLong  = @Long4
          set @useEndLat  = @Lat4
          
          set @useRatio = @thisRatio
          set @found = 1
        end
        else
        begin
         set @totalRatio = @totalRatio + @thisRatio
         set @lastLng = @Long4
         set @lastLat = @Lat4
         set @totalTravel = @totalTravel + @thisLen
        end
    end


    if (@Lat5 <> 0 and @found = 0) 
    begin
        set @thisLen = dbo.fnLineDistance(@long5, @lat5, @lastLng, @lastLat)
        set @thisRatio = @thisLen / @TotalLength

        if (@thisLen + @totalTravel >= @travelTarget)
        begin
          set @useStartLong  = @lastLng
          set @useStartLat  = @lastLat
          set @useEndLong  = @Long5
          set @useEndLat  = @Lat5
          
          set @useRatio = @thisRatio
          set @found = 1
        end
        else
        begin
         set @totalRatio = @totalRatio + @thisRatio
         set @lastLng = @Long5
         set @lastLat = @Lat5
         set @totalTravel = @totalTravel + @thisLen
        end    
    end


    if (@Lat6 <> 0 and @found = 0) 
    begin
        set @thisLen = dbo.fnLineDistance(@long6, @lat6, @lastLng, @lastLat)
        set @thisRatio = @thisLen / @TotalLength

        if (@thisLen + @totalTravel >= @travelTarget)
        begin
          set @useStartLong  = @lastLng
          set @useStartLat  = @lastLat
          set @useEndLong  = @Long6
          set @useEndLat  = @Lat6
          
          set @useRatio = @thisRatio
          set @found = 1
        end
        else
        begin
         set @totalRatio = @totalRatio + @thisRatio
         set @lastLng = @Long6
         set @lastLat = @Lat6
         set @totalTravel = @totalTravel + @thisLen
        end
    end



    if (@Lat7 <> 0 and @found = 0) 
    begin
        set @thisLen = dbo.fnLineDistance(@long7, @lat7, @lastLng, @lastLat)
        set @thisRatio = @thisLen / @TotalLength

        if (@thisLen + @totalTravel >= @travelTarget)
        begin
          set @useStartLong  = @lastLng
          set @useStartLat  = @lastLat
          set @useEndLong  = @Long7
          set @useEndLat  = @Lat7
          
          set @useRatio = @thisRatio
          set @found = 1
        end
        else
        begin
         set @totalRatio = @totalRatio + @thisRatio
         set @lastLng = @Long7
         set @lastLat = @Lat7
         set @totalTravel = @totalTravel + @thisLen
        end
    end




    if (@Lat8 <> 0 and @found = 0) 
    begin
        set @thisLen = dbo.fnLineDistance(@long8, @lat8, @lastLng, @lastLat)
        set @thisRatio = @thisLen / @TotalLength

        if (@thisLen + @totalTravel >= @travelTarget)
        begin
          set @useStartLong  = @lastLng
          set @useStartLat  = @lastLat
          set @useEndLong  = @Long8
          set @useEndLat  = @Lat8
          
          set @useRatio = @thisRatio
          set @found = 1
        end
        else
        begin
         set @totalRatio = @totalRatio + @thisRatio
         set @lastLng = @Long8
         set @lastLat = @Lat8
         set @totalTravel = @totalTravel + @thisLen
        end
    end




    if (@Lat9 <> 0 and @found = 0) 
    begin
        set @thisLen = dbo.fnLineDistance(@long9, @lat9, @lastLng, @lastLat)
        set @thisRatio = @thisLen / @TotalLength

        if (@thisLen + @totalTravel >= @travelTarget)
        begin
          set @useStartLong  = @lastLng
          set @useStartLat  = @lastLat
          set @useEndLong  = @Long9
          set @useEndLat  = @Lat9
          
          set @useRatio = @thisRatio
          set @found = 1
        end
        else
        begin
         set @totalRatio = @totalRatio + @thisRatio
         set @lastLng = @Long9
         set @lastLat = @Lat9
         set @totalTravel = @totalTravel + @thisLen
        end
    end




    if (@Lat10 <> 0 and @found = 0) 
    begin
        set @thisLen = dbo.fnLineDistance(@long2, @lat2, @lastLng, @lastLat)
        set @thisRatio = @thisLen / @TotalLength

        if (@thisLen + @totalTravel >= @travelTarget)
        begin
          set @useStartLong  = @lastLng
          set @useStartLat  = @lastLat
          set @useEndLong  = @Long10
          set @useEndLat  = @Lat10
          
          set @useRatio = @thisRatio
          set @found = 1
        end
        else
        begin
         set @totalRatio = @totalRatio + @thisRatio
         set @lastLng = @Long10
         set @lastLat = @Lat10
         set @totalTravel = @totalTravel + @thisLen
        end
    end
    

    if (@found = 0)
    begin
        set @thisLen = dbo.fnLineDistance(@tolng, @tolat, @lastLng, @lastLat)
        set @thisRatio = @thisLen / @TotalLength

        if (@thisLen + @totalTravel >= @travelTarget)
        begin
          set @useStartLong  = @lastLng
          set @useStartLat  = @lastLat
          set @useEndLong  = @tolng
          set @useEndLat  = @tolat
          
          set @useRatio = @thisRatio
          set @found = 1
        end
        else
        begin
         set @totalRatio = @totalRatio + @thisRatio
         set @lastLng = @tolng
         set @lastLat = @tolat
         set @totalTravel = @totalTravel + @thisLen
        end
    end

    declare @lon decimal(9,6)
    declare @lat decimal(9,6)

    declare @rel decimal(9,6)    

    set @rel = (@Ratio - @totalRatio) / @useRatio
        
    declare @lonDist decimal(9,6) 
    declare @latDist decimal(9,6)
    set @lonDist = @useEndLong - @useStartLong
    set @latDist = @useEndLat - @useStartLat
/*
    set @lon = @useStartLong + (@rel * @lonDist)
    set @lat = @useStartLat + (@rel * @latDist)
*/
    set @lon = @useEndLong - (@rel * @lonDist)
    set @lat = @useEndLat - (@rel * @latDist)
    
    insert into @LongLat    
        select @lon, @lat
    RETURN 
END





GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO





/*
© 2005 John Sample

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

*/

/*
This function will find the closest node to the given long/lat
It will usually return more than one row since the beginning/end of one node is almost always the beginning/end of at least one other
However, the Long/Lat returned for all records will almost always be the same unless we happen to query a point which is equidistant from 2 or more completely unchained nodes.
You can query the return table for distinct names to get the title of the intersection if one exists.
If you are using a database with more than one tiger/line county set, use the statel/stater and countyl/countyr FIPs codes for general location info
*/

CREATE   function fnReverseGeocode(@Longitude decimal(9,6), @Latitude decimal(9,6))
RETURNS @Location TABLE 
    (
    TLID varchar(50),
    Longitude Decimal(9,6), --These are the closest node points, they could be frlong/frlat or tolong/tolat
    Latitude Decimal(9,6), 
    fedirp varchar(2),
    fename varchar(50),
    fetype varchar(10),
    fraddr int,
    toaddr int,
    fraddl int,
    toaddl int,
    zipl int,
    zipr int,
    frlat int,
    frlong int,
    tolat int,
    tolong int,
    countyl int,
    countyr int,
    statel int,
    stater int
    )
AS
BEGIN

declare @fromdec int 
set @fromdec = 1000000
declare @todecimal decimal(9,6)
set @todecimal = 0.000001
declare @Lat int 
declare @Lng int

set @Lat = @Latitude * @fromdec
set @Lng = @Longitude * @fromdec

insert into @Location
select     TLID, frlong * @todecimal as Longitude, frlat  * @todecimal as Latitude, fedirp, fename, fetype, fraddr, toaddr, fraddl, toaddl, zipl, zipr, frlat, frlong, tolat, tolong, countyl, countyr, statel, stater 
    from tiger_01 where fename <> '' and fetype <> '' and abs(frlat - @Lat) +  abs(frlong - @Lng) = (select min(abs(frlat - @Lat) + abs(frlong - @Lng)) from tiger_01 where fename <> '' and fetype <> '' )
UNION
select TLID, tolong * @todecimal as Longitude, tolat * @todecimal as Latitude, fedirp, fename, fetype, fraddr, toaddr, fraddl, toaddl, zipl, zipr, frlat, frlong, tolat, tolong, countyl, countyr, statel, stater 
    from tiger_01 where fename <> '' and fetype <> '' and  abs(tolat - @Lat) +  abs(tolong - @Lng) = (select min(abs(tolat - @Lat) + abs(tolong - @Lng)) from tiger_01 where fename <> '' and fetype <> '' )

return
END





GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO





CREATE  FUNCTION dbo.fnTotalChainDistance
    (
        
        @FromLng decimal(9,6),
        @FromLat decimal(9,6),
         @Long1 decimal(9,6),
        @Lat1 decimal(9,6),
        @Long2 decimal(9,6),
        @Lat2 decimal(9,6),
        @Long3 decimal(9,6),
        @Lat3 decimal(9,6),
        @Long4 decimal(9,6),
        @Lat4 decimal(9,6),
        @Long5 decimal(9,6),
        @Lat5 decimal(9,6),
        @Long6 decimal(9,6),
        @Lat6 decimal(9,6),
        @Long7 decimal(9,6),
        @Lat7 decimal(9,6),
        @Long8 decimal(9,6),
        @Lat8 decimal(9,6),
        @Long9 decimal(9,6),
        @Lat9 decimal(9,6),
        @Long10 decimal(9,6),
        @Lat10 decimal(9,6),
        @ToLng decimal(9,6),
        @ToLat decimal(9,6)
    )

RETURNS @Distance  TABLE
    (
    TotalLong decimal(9,6), 
    TotalLat decimal(9,6)
    )
AS
BEGIN

    declare @latTotal decimal(9,6)
    declare @lngTotal decimal(9,6)
    set @latTotal = 0.0
    set @lngTotal = 0.0

    declare @lastLat decimal(9,6)
    declare @lastLng decimal(9,6)

    --Buffer vars so we know what to subtract with
    set @lastLat = @FromLat
    set @lastLng = @FromLng
    
    --we've got to do this 10 friggin times
    if (@Lat1 <> 0) 
    begin
        set @latTotal = @latTotal + Abs((@Lat1 - @lastLat))
        set @lngTotal = @lngTotal + Abs((@Long1  - @lastLng))
        set @lastLat = @Lat1
        set @lastLng = @Long1
    end
    if (@Lat2 <> 0) 
    begin
        set @latTotal = @latTotal + Abs((@Lat2  - @lastLat))
        set @lngTotal = @lngTotal + Abs((@Long2  - @lastLng))
        set @lastLat = @Lat2
        set @lastLng = @Long2
    end
    if (@Lat3 <> 0) 
    begin
        set @latTotal = @latTotal + Abs((@Lat3  - @lastLat))
        set @lngTotal = @lngTotal + Abs((@Long3  - @lastLng))
        set @lastLat = @Lat3
        set @lastLng = @Long3
    end
    if (@Lat4 <> 0) 
    begin
        set @latTotal = @latTotal + Abs((@Lat4  - @lastLat))
        set @lngTotal = @lngTotal + Abs((@Long4  - @lastLng))
        set @lastLat = @Lat4
        set @lastLng = @Long4
    end
    if (@Lat5 <> 0) 
    begin
        set @latTotal = @latTotal + Abs((@Lat5  - @lastLat))
        set @lngTotal = @lngTotal + Abs((@Long5  - @lastLng))
        set @lastLat = @Lat5
        set @lastLng = @Long5
    end
    if (@Lat6 <> 0) 
    begin
        set @latTotal = @latTotal + Abs((@Lat6  - @lastLat))
        set @lngTotal = @lngTotal + Abs((@Long6  - @lastLng))
        set @lastLat = @Lat6
        set @lastLng = @Long6
    end
    if (@Lat7 <> 0) 
    begin
        set @latTotal = @latTotal + Abs((@Lat7  - @lastLat))
        set @lngTotal = @lngTotal + Abs((@Long7 - @lastLng))
        set @lastLat = @Lat7
        set @lastLng = @Long7
    end
    if (@Lat8 <> 0) 
    begin
        set @latTotal = @latTotal + Abs((@Lat8  - @lastLat))
        set @lngTotal = @lngTotal + Abs((@Long8  - @lastLng))
        set @lastLat = @Lat8
        set @lastLng = @Long8
    end
    if (@Lat9 <> 0) 
    begin
        set @latTotal = @latTotal + Abs((@Lat9 - @lastLat))
        set @lngTotal = @lngTotal + Abs((@Long9  - @lastLng))
        set @lastLat = @Lat9
        set @lastLng = @Long9
    end
    if (@Lat10 <> 0) 
    begin
        set @latTotal = @latTotal + Abs((@Lat10  - @lastLat))
        set @lngTotal = @lngTotal + Abs((@Long10  - @lastLng))
        set @lastLat = @Lat10
        set @lastLng = @Long10
    end
    
    set @latTotal = @latTotal + Abs((@ToLat - @lastLat))
    set @lngTotal = @lngTotal + Abs((@ToLng  - @lastLng))

    insert into @Distance
        select @lngTotal, @latTotal
    RETURN 
END





GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

