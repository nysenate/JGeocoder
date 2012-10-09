import shapefile
import csv


def main():
    print "Started"
    inputfolders=["C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36007_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36009_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36011_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36013_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36015_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36017_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36019_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36021_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36023_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36025_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36027_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36029_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36031_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36033_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36035_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36037_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36039_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36041_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36043_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36045_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36047_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36049_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36051_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36053_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36055_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36057_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36059_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36061_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36063_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36065_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36067_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36069_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36071_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36073_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36075_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36077_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36079_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36081_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36083_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36085_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36087_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36089_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36091_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36093_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36095_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36097_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36099_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36101_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36103_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36105_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36107_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36109_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36111_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36113_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36115_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36117_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36119_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36121_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36123_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36001_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36003_addrfeat",
"C:\Users\Bobby\Downloads\NY_addrfeat\tl_2011_36005_addrfeat"]
    for each in inputfolders:
        convertShapefile(each)
    


def countMax(folderPath, max):
    sf = shapefile.Reader(folderPath.replace('\t','\\t')+"\\t"+folderPath.rsplit("\t")[1])
    shapes = sf.shapes()
    for each in shapes:
        if len(each.points)>max:
            max=len(each.points)
    print max
def convertShapefile(folderPath):
    sf = shapefile.Reader(folderPath.replace('\t','\\t')+"\\t"+folderPath.rsplit("\t")[1])
    shapes = sf.shapes()
    fields=sf.fields[1:]
    records=sf.records()
    fullfields=[]
    for each in fields:
        fullfields.append(each)
    fullfields.append("[BBOX text")
    fullfields.append("NUMPARTS ")
    fullfields.append("SHAPETYPE")
    fullfields.append("LATLONGPAIRS")
    outputFile=open("C:\Users\Bobby\Downloads\NY_addrfeat\CSV\TIGER_NY.csv", "ab")
    output=csv.writer(outputFile)
    #output.writerow(fullfields)
    for i in range(len(records)):
        row=[]
        for j in range(len(fields)):
            row.append(records[i][j])
        newbbox=""
        for each in shapes[i].bbox:
            newbbox+=str(each)+";"
        row.append(newbbox[:-1])
        row.append(shapes[i].parts)
        row.append(shapes[i].shapeType)
        pointString=""
        for each in shapes[i].points:
            pointString+="("+str(each[1])+";"+str(each[0])+"):"
        row.append(pointString[:-1])
        output.writerow(row)
    outputFile.close()
    print "done"

    
if __name__ == "__main__":
    main()
