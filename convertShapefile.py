import shapefile
import csv

import os,sys

def main():
    with open(sys.argv[2], "wb") as outputFile:
        for path in os.listdir(os.path.abspath(sys.argv[1])):
            path = os.path.join(sys.argv[1],path)
            if os.path.isdir(path):
                convertShapefile(path, outputFile)

def convertShapefile(folderPath, outputFile):
    shp_path = os.path.join(folderPath, os.path.basename(folderPath)+'.shp')
    sf = shapefile.Reader(shp_path)
    shapes = sf.shapes()
    fields=sf.fields[1:]
    records=sf.records()
    fullfields=records[:len(fields)]+["BBOX text","NUMPARTS","SHAPETYPE","LATLONGPAIRS"]

    csv_writer = csv.writer(outputFile)
    # csv_writer.writerow(fullfields)
    for shape, record in zip(shapes, records):
        bbox = ";".join(str(point) for point in shape.bbox)
        num_parts = len(shape.parts)
        shape_type =  shape.shapeType
        point_str = ":".join("({0};{1})".format(p[1],p[0]) for p in shape.points)
        full_record = record+[bbox, num_parts,shape_type,point_str]
        csv_writer.writerow(full_record)

    print("Done with {0}".format(folderPath))


if __name__ == "__main__":
    main()
