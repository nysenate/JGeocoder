import shapefile
import csv

import os,sys

def main():
    for path in os.listdir(os.path.abspath(sys.argv[1])):
        path = os.path.join(sys.argv[1],path)
        if os.path.isdir(path):
            convertShapefile(path)

def convertShapefile(folderPath):
    shp_path = os.path.join(folderPath, os.path.basename(folderPath)+'.shp')
    sf = shapefile.Reader(shp_path)
    shapes = sf.shapes()
    fields=sf.fields[1:]
    records=sf.records()
    fullfields=records[:len(fields)]+["BBOX text","NUMPARTS","SHAPETYPE","LATLONGPAIRS"]

    with open(sys.argv[2], "ab") as outputFile:
        csv_writer = csv.writer(outputFile)
        # csv_writer.writerow(fullfields)
        for shape, record in zip(shapes, records):
            bbox = ";".join(str(point) for point in shape.bbox)
            num_parts = len(shape.parts)
            shape_type =  shape.shapeType
            point_str = ":".join("({0};{1})".format(p[1],p[0]) for p in shape.points)
            csv_writer.writerow(record+[bbox, num_parts,shape_type,point_str])

    print "done"


if __name__ == "__main__":
    main()
