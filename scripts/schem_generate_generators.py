from nbt import nbt
import sys
import os
import ntpath
import json

folder = sys.argv[1]

# noinspection DuplicatedCode
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
    stuff = {
        "type": "dimdoors:schematic",
        "builder": {
            "type": "dimdoors:lazy_gen_pocket"
        },
        "modifiers": [
            # {
            #     "type": "dimdoors:rift_data",
            #     "ids": [0],
            #     "rift_data": "dimdoors:rift_data/pocket_entrance"
            # },
            # {
            #     "type": "dimdoors:rift_data",
            #     "ids": [1],
            #     "rift_data": "dimdoors:rift_data/default_dungeon"
            # }
        ]
    }
    nbt_data = nbt.NBTFile(name, "rb")
    thing_name = path_leaf(name).replace(".schem", "")
    bes = nbt_data["BlockEntities"]
    modifiers = []
    for be in bes:
        id = be["Id"]
        if (id.value == "dimdoors:entrance_rift"):
            dest = be["data"]["destination"]
            thing_id = dest["id"]
            if thing_id == 0:
                modifiers.append({
                    "type": "dimdoors:rift_data",
                    "ids": [0],
                    "rift_data": "dimdoors:rift_data/pocket_entrance"
                })
            else:
                modifiers.append({
                    "type": "dimdoors:rift_data",
                    "ids": [thing_id.value],
                    "rift_data": "dimdoors:rift_data/default_dungeon"
                })
    stuff["id"] = "dungeon/" + thing_name
    stuff["modifiers"] = modifiers
    file = open(f"./generated/{thing_name}.json", "x")
    file.write(json.dumps(stuff, indent = 2))
