from nbt import nbt
import sys
import os
import ntpath

folder = sys.argv[1]
x = "  {\n    \"id\": \"dungeon/%s\",\n    \"type\": \"dimdoors:id\"\n  },"

list_of_files = {}


def walk_through_files(path, file_extension='.schem'):
    for (dirpath, dirnames, filenames) in os.walk(path):
        for filename in filenames:
            if filename.endswith(file_extension):
                yield os.path.join(dirpath, filename)


def path_leaf(path):
    head, tail = ntpath.split(path)
    return tail or ntpath.basename(head)


for name in walk_through_files(folder):
    nbt_data = nbt.NBTFile(name, "rb")
    file_name = path_leaf(name).replace(".schem", "")
    print(x.replace("%s", file_name))
