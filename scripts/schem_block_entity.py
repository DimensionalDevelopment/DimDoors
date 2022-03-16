from nbt import nbt
import sys

path = sys.argv[1]

nbt_data = nbt.NBTFile(path, "rb")
bes = nbt_data["BlockEntities"]
for be in bes:
	id = be["Id"]
	if (id.value == "dimdoors:entrance_rift"):
		print(id)
		print(be["data"]["destination"].pretty_tree())
		print('')
