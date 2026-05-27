# Dimensional Doors Datapack JSON

This document describes the server-data JSON formats used by Dimensional Doors for pocket generation, rift behavior, and dimensional door data.

The working reference examples are in:

```text
common/src/main/resources/resourcepacks/default
```

The `classic` pack is legacy content for old pocket layouts. This document treats the `default` pack as the reference for the current JSON shape.

## Pack Layout

A datapack that adds Dimensional Doors pocket data uses normal Minecraft datapack layout:

```text
my_pack/
  pack.mcmeta
  data/
    <namespace>/
      pockets/
        generators/
          <path>.json
        virtual/
          <path>.json
        groups/
          <path>.json
        rift_data/
          <path>.json
        schematic/
          <path>.schem
      door/
        data/
          <door_item_path>.json
```

Resource IDs are derived from `data/<namespace>/<registry_path>/<path>.json`.

Examples:

```text
data/example/pockets/generators/rooms/start.json
```

becomes:

```text
example:rooms/start
```

```text
data/example/pockets/schematic/rooms/start.schem
```

becomes:

```text
example:rooms/start
```

Dimensional Doors uses these dynamic registries:

| Folder | Registry | Purpose |
| --- | --- | --- |
| `pockets/generators` | `minecraft:pockets/generators` | Concrete pocket generators. |
| `pockets/virtual` | `minecraft:pockets/virtual` | Weighted selectors and references to generators. |
| `pockets/groups` | `minecraft:pockets/groups` | Named virtual-pocket selectors used by doors and built-in generation. |
| `pockets/rift_data` | `minecraft:pockets/rift_data` | Reusable rift destination and link data. |
| `door/data` | `minecraft:door/data` | Door-item behavior data. |

The `pockets/schematic` folder is loaded separately. It stores `.schem` files, not JSON.

Unknown JSON fields are ignored by the Minecraft codecs used here. Some default-pack files still contain historical fields that no longer affect current code. This document marks those cases where they matter.

## Resource References

Most references use normal resource IDs:

```json
"dimdoors:dungeon/waiting_room"
```

If the namespace is omitted in a Minecraft resource key context, Minecraft may treat it as `minecraft`. For Dimensional Doors content, write the namespace explicitly.

Holder fields usually accept a resource ID string. Some holder fields also accept an inline object, as shown by the built-in `private/default.json` generator using inline `rift_data`.

Prefer resource IDs for reusable data:

```json
{
  "type": "dimdoors:rift_data",
  "ids": [0],
  "rift_data": "dimdoors:pocket_entrance"
}
```

Use inline objects for one-off data:

```json
{
  "type": "dimdoors:rift_data",
  "ids": [0],
  "rift_data": {
    "destination": {
      "type": "dimdoors:pocket_exit"
    }
  }
}
```

## Equations

Several numeric fields use the Dimensional Doors equation parser instead of plain numbers.

Equation fields accept a string or an integer:

```json
"width": "5 + 16 * public_size"
```

```json
"weight": 5
```

Do not rely on decimal JSON numbers for equation fields. The equation codec accepts strings and integers.

Available operators and functions:

| Syntax | Meaning |
| --- | --- |
| `+`, `-`, `*`, `/`, `%`, `^` | Arithmetic. |
| `==`, `<`, `>`, `<=`, `>=` | Comparisons. Return `1` for true and `0` for false. |
| `&&`, `||` | Boolean operators. Nonzero is true. |
| `condition ? a : b` | Ternary conditional. |
| `H(x)` | Heaviside step. Returns `1` when `x >= 0`, otherwise `0`. |
| `floor(x)` | Floor. |
| `ceil(x)` | Ceiling. |
| `min(a, b, ...)` | Minimum. At least two arguments. |
| `max(a, b, ...)` | Maximum. At least two arguments. |
| `clamp(x, min, max)` | Clamp. |
| `random()` | Random double in `[0, 1)`. |

Pocket-generation equations receive:

| Variable | Meaning |
| --- | --- |
| `depth` | The virtual pocket depth being generated. |
| `public_size` | Current config value for public pocket size. |
| `private_size` | Current config value for private pocket size. |

Modifier equations that run after a pocket exists may also receive:

| Variable | Meaning |
| --- | --- |
| `id` | Pocket ID. |
| `originX`, `originY`, `originZ` | Pocket origin in the target dimension. |
| `width`, `height`, `length` | Current pocket size. |
| `depth` | Pocket virtual depth. |

## Schematic Files

Schematics are compressed `.schem` files loaded from:

```text
data/<namespace>/pockets/schematic/<path>.schem
```

The schematic ID is:

```text
<namespace>:<path>
```

Example from the built-in pack:

```text
common/src/main/resources/resourcepacks/default/data/dimdoors/pockets/schematic/example/purpur_hallway.schem
```

has ID:

```text
dimdoors:example/purpur_hallway
```

A schematic generator references that schematic through its `id` field:

```json
{
  "type": "dimdoors:schematic",
  "id": "dimdoors:example/purpur_hallway",
  "modifiers": []
}
```

The generator JSON file ID and schematic ID can be the same, but they are separate concepts. The generator's registry ID comes from the generator JSON file path. The generator's `id` field points to a `.schem` template.

Some built-in schematic generator files contain top-level `offset_x`, `offset_y`, and `offset_z`. Those are legacy fields. The current `dimdoors:schematic` generator codec does not read them. Use the `dimdoors:offset` modifier when a pocket needs an actual origin offset.

## Pocket Generators

Pocket generators live in:

```text
data/<namespace>/pockets/generators/<path>.json
```

They create actual pocket contents. The supported generator types are:

| Type | Purpose |
| --- | --- |
| `dimdoors:schematic` | Places a `.schem` template into a new pocket. |
| `dimdoors:void` | Creates an empty pocket volume sized by equations. |

### Common Generator Fields

All generator JSON objects use these fields:

| Field | Required | Type | Default | Meaning |
| --- | --- | --- | --- | --- |
| `type` | Yes | Resource ID | None | Generator type. |
| `builder` | No | Pocket builder object | `dimdoors:pocket` | Controls the pocket class and builder add-ons. |
| `weight` | No | Equation | `5` | Weight used when this generator is selected through virtual pockets. |
| `setup_loot` | No | Boolean | `false` | If true, empty supported containers receive Dimensional Doors loot tables. |
| `modifiers` | Yes | Array of modifier objects or references | None | Modifies the pocket before and after placement. Use `[]` for no modifiers. |
| `tags` | No | Array of strings | `[]` | Tags used by `dimdoors:tag` virtual pockets. |

Minimal schematic generator:

```json
{
  "type": "dimdoors:schematic",
  "id": "example:rooms/start",
  "modifiers": []
}
```

Minimal void generator:

```json
{
  "type": "dimdoors:void",
  "width": "16",
  "height": "8",
  "length": "16",
  "modifiers": []
}
```

### `dimdoors:schematic`

Required fields:

| Field | Type | Meaning |
| --- | --- | --- |
| `id` | Resource ID | Schematic template ID from `pockets/schematic`. |

Optional fields:

| Field | Type | Default | Meaning |
| --- | --- | --- | --- |
| `placement_type` | String | `section_no_update` | Block placement strategy. |

Valid `placement_type` values:

| Value |
| --- |
| `section_no_update_queue_block_entity` |
| `section_no_update` |
| `section_update` |
| `set_block_state` |
| `set_block_state_queue_block_entity` |

Example from the default pack:

```json
{
  "type": "dimdoors:schematic",
  "id": "dimdoors:example/purpur_hallway",
  "modifiers": [
    {
      "type": "dimdoors:shell",
      "layers": [
        {
          "block_state": "dimdoors:pink_ancient_fabric",
          "thickness": "1"
        }
      ]
    }
  ],
  "tags": ["nether"]
}
```

### `dimdoors:void`

Required fields:

| Field | Type | Meaning |
| --- | --- | --- |
| `width` | Equation | Pocket X size. |
| `height` | Equation | Pocket Y size. |
| `length` | Equation | Pocket Z size. |

Example from the default pack:

```json
{
  "type": "dimdoors:void",
  "width": "5 + 16 * public_size",
  "height": "5 + 16 * min(15, public_size)",
  "length": "5 + 16 * public_size",
  "modifiers": [
    {
      "type": "dimdoors:shell",
      "layers": [
        {
          "block_state": "dimdoors:black_fabric",
          "thickness": "4"
        },
        {
          "block_state": "dimdoors:black_ancient_fabric",
          "thickness": "1"
        }
      ]
    }
  ]
}
```

## Pocket Builders

Generators may include a `builder` object:

```json
{
  "builder": {
    "type": "dimdoors:private_pocket",
    "addons": [
      {
        "type": "dimdoors:dyeable"
      }
    ]
  },
  "type": "dimdoors:void",
  "width": "5 + 16 * private_size",
  "height": "5 + 16 * min(15, private_size)",
  "length": "5 + 16 * private_size",
  "modifiers": []
}
```

Supported builder types:

| Type | Meaning |
| --- | --- |
| `dimdoors:pocket` | Normal pocket. |
| `dimdoors:private_pocket` | Private pocket. Supports dyeable behavior. |
| `dimdoors:id_reference` | Reference pocket builder. This is not normally useful for new generated pockets. |

Common builder fields:

| Field | Required | Type | Default | Meaning |
| --- | --- | --- | --- | --- |
| `type` | Yes | Resource ID | None | Builder type. |
| `addons` | No | Array | `[]` | Builder add-ons applied to the created pocket. |

### Builder Add-Ons

Builder add-ons use `type`.

#### `dimdoors:dyeable`

Adds dyeable behavior. This applies to private pockets.

Fields:

| Field | Required | Type | Default | Meaning |
| --- | --- | --- | --- | --- |
| `dye_color` | No | Dye color string | `none` | Initial dye color. |

Valid dye colors:

```text
white, orange, magenta, light_blue, yellow, lime, pink, gray,
light_gray, cyan, purple, blue, brown, green, red, black, none
```

Example:

```json
{
  "type": "dimdoors:dyeable",
  "dye_color": "white"
}
```

#### `dimdoors:force_loaded`

Adds force-loaded pocket behavior.

Fields: none.

Example:

```json
{
  "type": "dimdoors:force_loaded"
}
```

#### `dimdoors:prevent_block_modification`

Prevents non-creative block placement and modification inside the pocket.

Fields: none.

Example:

```json
{
  "type": "dimdoors:prevent_block_modification"
}
```

#### `dimdoors:environment`

Overrides environment rendering/behavior data for the pocket.

Fields:

| Field | Required | Type | Default | Meaning |
| --- | --- | --- | --- | --- |
| `environment` | No | Environment object | `dimdoors:empty` | Environment definition. |

Example from the default pack:

```json
{
  "type": "dimdoors:environment",
  "environment": {
    "type": "dimdoors:overworld",
    "day_time": 6000,
    "cloud_height": 250
  }
}
```

Supported environment types:

| Type | Fields |
| --- | --- |
| `dimdoors:empty` | No fields. |
| `dimdoors:end` | No fields. |
| `dimdoors:overworld` | `day_time`, `moon_phase`, `skyColor`, `rain_level`, `precipitation`, `thunder_level`, `cloud_height`, `cloud_color`. |
| `dimdoors:complex` | `sky`, `weather`, `cloud`. |

`dimdoors:overworld` fields:

| Field | Type | Default |
| --- | --- | --- |
| `day_time` | Long | `12000` |
| `moon_phase` | Integer | `0` |
| `skyColor` | Vec3 array | `[0.486, 0.654, 1.0]` |
| `rain_level` | Float | `0.0` |
| `precipitation` | Precipitation enum | `none` |
| `thunder_level` | Float | `0.0` |
| `cloud_height` | Float | `128.0` |
| `cloud_color` | Vec3 array | `[1.0, 1.0, 1.0]` |

`dimdoors:complex` composes separate typed objects:

```json
{
  "type": "dimdoors:complex",
  "sky": {
    "type": "dimdoors:overworld",
    "day_time": 6000
  },
  "weather": {
    "type": "dimdoors:overworld",
    "precipitation": "rain",
    "rainLevel": 1.0
  },
  "cloud": {
    "type": "dimdoors:overworld",
    "height": 250,
    "color": [1.0, 1.0, 1.0]
  }
}
```

Supported `sky` types:

| Type | Fields |
| --- | --- |
| `dimdoors:empty` | No fields. |
| `dimdoors:end` | No fields. |
| `dimdoors:overworld` | `day_time`, `moon_phase`, `skyColor`, `rain_level`, `thunder_level`. |

Supported `weather` types:

| Type | Fields |
| --- | --- |
| `dimdoors:empty` | No fields. |
| `dimdoors:overworld` | `precipitation`, `rainLevel`. |

Supported `cloud` types:

| Type | Fields |
| --- | --- |
| `dimdoors:empty` | No fields. |
| `dimdoors:overworld` | `height`, `color`. |

## Modifiers

Generator `modifiers` run in the order listed in the generator JSON.

Each modifier is applied twice:

1. Before the pocket is placed, against the pocket builder.
2. After the pocket is placed, against the placed pocket's rifts and blocks.

Most modifiers only do work in one of those passes:

| Modifier | Builder pass | Placed-pocket pass |
| --- | --- | --- |
| `dimdoors:shell` | Expands the expected pocket size and offsets the origin for each layer. | Draws the shell blocks. |
| `dimdoors:offset` | Offsets the pocket origin. | No effect. |
| `dimdoors:rift_data` | No effect. | Applies rift data to matching rift IDs and consumes those IDs. |
| `dimdoors:relative` | No effect. | Links matching rift IDs to each other and consumes linked IDs. |
| `dimdoors:door` | No effect. | Places a dimensional door, creates an entrance rift, and registers that rift with the rift manager. |
| `dimdoors:pocket_entrance` | No effect. | Converts one rift ID into a `dimdoors:pocket_entrance` marker and consumes that ID. |
| `dimdoors:template` | No effect. | Converts matching rift IDs into `dimdoors:template` targets and consumes those IDs. |

Use an empty list if no modifiers are needed:

```json
"modifiers": []
```

Built-in generator files use inline modifier objects:

```json
{
  "type": "dimdoors:rift_data",
  "ids": [0],
  "rift_data": "dimdoors:pocket_entrance"
}
```

The loader codec is a holder codec, so registered modifier holders can also be referenced by ID if a registry entry exists. The built-in datapack content does not use modifier reference files, so inline objects are the documented datapack form.

### Rift IDs And Consumption

Schematic rifts are discovered after the pocket is placed. The `RiftManager` maps only rifts whose destination is:

```json
{
  "type": "dimdoors:id_marker",
  "id": 0
}
```

The integer `id` is what `ids`, `id`, `point_a`, and `point_b` refer to. IDs below `0` are ignored by the manager.

When a modifier consumes an ID, later modifiers cannot use that same ID through the rift manager. Order matters. For example, this applies entrance data to rift `0`; the later `relative` modifier cannot still use `0`:

```json
[
  {
    "type": "dimdoors:rift_data",
    "ids": [0],
    "rift_data": "dimdoors:pocket_entrance"
  },
  {
    "type": "dimdoors:relative",
    "point_a": 0,
    "point_b": 1
  }
]
```

IDs are consumed by:

| Modifier | Consumed IDs |
| --- | --- |
| `dimdoors:rift_data` | Every matching ID in `ids`. |
| `dimdoors:relative` | `point_a`, and `point_b` when `connection` is `both`. |
| `dimdoors:pocket_entrance` | `id`. |
| `dimdoors:template` | Every matching ID in `ids`. |

`dimdoors:door` can add a new rift to the manager. If its `rift_data` contains an `id_marker`, that new rift can be consumed by a later modifier. The default gold pocket uses this pattern: the door creates ID `0`, then `dimdoors:pocket_entrance` consumes ID `0`.

Supported modifiers:

| Type | Purpose |
| --- | --- |
| `dimdoors:shell` | Draws one or more block shells around the pocket. |
| `dimdoors:rift_data` | Applies rift data to schematic rifts by numeric ID. |
| `dimdoors:relative` | Links two rifts in the same pocket by numeric ID. |
| `dimdoors:offset` | Offsets the pocket origin before placement. |
| `dimdoors:door` | Places a dimensional door and creates an entrance rift. |
| `dimdoors:pocket_entrance` | Converts a rift ID into a weighted pocket entrance marker. |
| `dimdoors:template` | Makes rift IDs generate a selected virtual pocket. |

### `dimdoors:shell`

`dimdoors:shell` surrounds the generated pocket with one or more solid block layers.

Fields:

| Field | Required | Type | Default | Meaning |
| --- | --- | --- | --- | --- |
| `layers` | No | Array of layers | `[]` | Shell layers, applied inside-to-outside. |
| `box_to_draw_around` | No | Bounding box array | Current pocket box | Alternate box to shell around during the placed-pocket pass. |

Layer fields:

| Field | Required | Type | Default | Meaning |
| --- | --- | --- | --- | --- |
| `block_state` | Yes | Block state string | None | Block state to place. |
| `thickness` | No | Equation | `1` | Layer thickness in blocks. |

During the builder pass, each layer expands the expected pocket size by `2 * thickness` on each axis and offsets the origin by `thickness` on each axis. During the placed-pocket pass, each layer is drawn as a solid shell outside the previous layer.

Layer order matters. The first layer is closest to the pocket. Later layers are farther outward.

Block state strings use Minecraft block-state syntax:

```json
"minecraft:oak_log[axis=x]"
```

`box_to_draw_around` uses Minecraft's bounding-box codec:

```json
[0, 0, 0, 15, 7, 15]
```

The values are:

```text
[minX, minY, minZ, maxX, maxY, maxZ]
```

When `box_to_draw_around` is present, it is moved by the pocket origin before drawing. It does not change the builder-pass size expansion.

Example:

```json
{
  "type": "dimdoors:shell",
  "layers": [
    {
      "block_state": "dimdoors:black_fabric",
      "thickness": "4"
    },
    {
      "block_state": "dimdoors:black_ancient_fabric",
      "thickness": "1"
    }
  ]
}
```

Example with a custom draw box:

```json
{
  "type": "dimdoors:shell",
  "box_to_draw_around": [0, 0, 0, 15, 7, 15],
  "layers": [
    {
      "block_state": "minecraft:deepslate_tiles",
      "thickness": "2"
    }
  ]
}
```

### `dimdoors:rift_data`

Applies `RiftData` to rifts whose schematic/block-entity ID is listed in `ids`.

Fields:

| Field | Required | Type | Default | Meaning |
| --- | --- | --- | --- | --- |
| `ids` | Yes | Array of integers | None | Rift IDs to consume. |
| `rift_data` | No | Rift data holder | Omitted | Data to apply. If omitted, the rift destination becomes `dimdoors:none`. |

`rift_data` accepts either a resource ID from `pockets/rift_data` or an inline rift data object.

Reference example:

```json
{
  "type": "dimdoors:rift_data",
  "ids": [0],
  "rift_data": "dimdoors:pocket_entrance"
}
```

Inline example:

```json
{
  "type": "dimdoors:rift_data",
  "ids": [0],
  "rift_data": {
    "destination": {
      "type": "dimdoors:escape",
      "canEscapeLimbo": true
    }
  }
}
```

Clear a rift by omitting `rift_data`:

```json
{
  "type": "dimdoors:rift_data",
  "ids": [3]
}
```

That consumes rift ID `3` and sets its destination to `dimdoors:none`.

### `dimdoors:relative`

Links one rift to another rift in the same generated pocket.

Fields:

| Field | Required | Type | Default | Meaning |
| --- | --- | --- | --- | --- |
| `point_a` | Yes | Integer | None | Source rift ID. |
| `point_b` | Yes | Integer | None | Target rift ID. |
| `connection` | No | `both` or `one_way` | `both` | Whether `point_b` also links back to `point_a`. |

Behavior:

| `connection` | Result |
| --- | --- |
| `both` | `point_a` links to `point_b`, and `point_b` links to `point_a`. Both IDs are consumed. |
| `one_way` | `point_a` links to `point_b`. Only `point_a` is consumed. |

The modifier only acts when both IDs still exist. If either ID was already consumed by an earlier modifier, no link is created.

Example:

```json
{
  "type": "dimdoors:relative",
  "point_a": 1,
  "point_b": 7,
  "connection": "both"
}
```

One-way example:

```json
{
  "type": "dimdoors:relative",
  "point_a": 2,
  "point_b": 6,
  "connection": "one_way"
}
```

### `dimdoors:offset`

Offsets the generated pocket origin before placement.

Fields:

| Field | Required | Type | Default |
| --- | --- | --- | --- |
| `offsetX` | No | Equation | `0` |
| `offsetY` | No | Equation | `0` |
| `offsetZ` | No | Equation | `0` |

The equations are evaluated from the pocket generation context before the pocket exists. They can use generation variables such as `depth`, `public_size`, and `private_size`.

The resulting values are cast to integers.

Example:

```json
{
  "type": "dimdoors:offset",
  "offsetX": "0",
  "offsetY": "4",
  "offsetZ": "0"
}
```

This modifier uses `offsetX`, `offsetY`, and `offsetZ`. Top-level `offset_x`, `offset_y`, and `offset_z` on a generator are legacy fields and are ignored by current code.

### `dimdoors:door`

Places a dimensional door and creates an entrance rift.

Fields:

| Field | Required | Type | Meaning |
| --- | --- | --- | --- |
| `facing` | Yes | Horizontal direction | Door facing. Valid values are `north`, `south`, `east`, `west`. |
| `door_type` | Yes | Block ID | Must resolve to a `DimensionalDoorBlock`. |
| `rift_data` | Yes | Rift data holder | Rift data placed on the door's entrance rift. |
| `x` | Yes | Equation | X offset from pocket origin. |
| `y` | Yes | Equation | Y offset from pocket origin. |
| `z` | Yes | Equation | Z offset from pocket origin. |

The lower half of the door is placed at:

```text
pocket_origin + (x, y, z)
```

The upper half is placed one block above it.

The position equations run after the pocket exists, so they can use pocket variables:

| Variable | Meaning |
| --- | --- |
| `id` | Pocket ID. |
| `originX`, `originY`, `originZ` | Pocket origin. |
| `width`, `height`, `length` | Pocket size. |
| `depth` | Pocket virtual depth. |

`rift_data` may be a reference or inline object. The default gold pocket uses inline `rift_data` with an `id_marker`, then a later `dimdoors:pocket_entrance` modifier consumes that ID.

Example:

```json
{
  "type": "dimdoors:door",
  "door_type": "dimdoors:block_ag_dim_minecraft_iron_door",
  "facing": "north",
  "x": "width/2",
  "y": "0",
  "z": "-1",
  "rift_data": "dimdoors:public_entrance"
}
```

Example using an inline ID marker:

```json
{
  "type": "dimdoors:door",
  "door_type": "dimdoors:block_ag_dim_dimdoors_gold_door",
  "facing": "north",
  "x": "width/2",
  "y": "0",
  "z": "-1",
  "rift_data": {
    "destination": {
      "type": "dimdoors:id_marker",
      "id": 0
    }
  }
}
```

### `dimdoors:pocket_entrance`

Marks an existing rift ID as the generated pocket entrance.

Fields:

| Field | Required | Type | Meaning |
| --- | --- | --- | --- |
| `id` | Yes | Integer | Rift ID to consume. |

Example:

```json
{
  "type": "dimdoors:pocket_entrance",
  "id": 0
}
```

This modifier sets the rift destination to a `dimdoors:pocket_entrance` marker with a default weight of `1.0` and an `ifDestination` of `dimdoors:pocket_exit`.

Equivalent target written out:

```json
{
  "type": "dimdoors:pocket_entrance",
  "weight": 1.0,
  "ifDestination": {
    "type": "dimdoors:pocket_exit"
  }
}
```

During final pocket setup, one entrance marker in the pocket is selected by weight. The selected marker is registered as the pocket entrance and becomes its `ifDestination`. Any other entrance markers become their `otherwiseDestination`.

### `dimdoors:template`

Makes selected rifts generate a virtual pocket.

Fields:

| Field | Required | Type | Meaning |
| --- | --- | --- | --- |
| `templateId` | Yes | Virtual pocket holder | Virtual pocket to generate. |
| `ids` | Yes | Array of integers | Rift IDs to consume. |

Example:

```json
{
  "type": "dimdoors:template",
  "ids": [2],
  "templateId": "dimdoors:lab/labfinal"
}
```

The placed rift destination becomes:

```json
{
  "type": "dimdoors:template",
  "template": "dimdoors:lab/labfinal"
}
```

The modifier field is named `templateId`. The virtual target field is named `template`.

## Virtual Pockets And Groups

Virtual pockets live in:

```text
data/<namespace>/pockets/virtual/<path>.json
```

Groups live in:

```text
data/<namespace>/pockets/groups/<path>.json
```

Both folders use the same JSON format. The difference is how the IDs are used. Built-in code refers to groups such as:

| Group ID | Use |
| --- | --- |
| `dimdoors:dungeon` | Default dungeon generation. |
| `dimdoors:public` | Public pocket generation. |
| `dimdoors:private` | Private pocket generation. |
| `dimdoors:gold` | Gold-door dungeon generation. |
| `dimdoors:myth` | Myth-tag dungeon generation. |
| `dimdoors:nether` | Nether-tag dungeon generation. |

A virtual pocket JSON can be either:

1. A single typed object.
2. An array of virtual pocket objects.

Arrays are weighted lists. Each child contributes its own weight.

Example array from `groups/dungeon.json`:

```json
[
  {
    "type": "dimdoors:path",
    "path": "dimdoors:dungeon/"
  },
  {
    "type": "dimdoors:id",
    "id": "dimdoors:lab/lab_hallway"
  }
]
```

### `dimdoors:id`

Directly references one pocket generator.

Fields:

| Field | Required | Type | Default | Meaning |
| --- | --- | --- | --- | --- |
| `id` | Yes | Generator ID | None | Generator to select. |
| `weight` | No | Equation | `5` | Selector weight. |

Example:

```json
{
  "type": "dimdoors:id",
  "id": "dimdoors:example/purpur_hallway"
}
```

### `dimdoors:tag`

Selects a random weighted generator whose `tags` match the filter.

Fields:

| Field | Required | Type | Default | Meaning |
| --- | --- | --- | --- | --- |
| `required` | No | Array of strings | `[]` | Required generator tags. |
| `blackList` | No | Array of strings | `[]` | Tags that exclude generators. Case-sensitive field name. |
| `exact` | No | Boolean | `false` | If true, the generator must have exactly the required tags. |
| `weight` | No | Equation | `5` | Selector weight. |

Example:

```json
{
  "type": "dimdoors:tag",
  "required": ["myth"]
}
```

### `dimdoors:path`

Selects virtual pockets under a registry path prefix.

Fields:

| Field | Required | Type | Meaning |
| --- | --- | --- | --- |
| `path` | Yes | Resource ID prefix | Virtual pocket ID prefix to include. |

Example:

```json
{
  "type": "dimdoors:path",
  "path": "dimdoors:dungeon/"
}
```

This matches virtual pockets whose path starts with `dungeon/` in the `dimdoors` namespace. The code also accepts `minecraft` namespace entries while scanning.

### `dimdoors:conditional`

Chooses the first child pocket whose equation condition evaluates to true.

Fields:

| Field | Required | Type | Meaning |
| --- | --- | --- | --- |
| `pockets` | Yes | Array | Ordered conditional entries. |

Each entry has:

| Field | Required | Type | Meaning |
| --- | --- | --- | --- |
| `condition` | Yes | Equation | Nonzero means true. |
| `pocket` | Yes | Virtual pocket object | Returned when condition is true. |

Example:

```json
{
  "type": "dimdoors:conditional",
  "pockets": [
    {
      "condition": "depth > 10",
      "pocket": {
        "type": "dimdoors:tag",
        "required": ["deep"]
      }
    },
    {
      "condition": "1",
      "pocket": {
        "type": "dimdoors:tag",
        "required": ["shallow"]
      }
    }
  ]
}
```

### `dimdoors:none`

Represents no virtual pocket.

Fields: none.

This is mostly useful as a fallback object. Trying to place it throws an error, so do not make active generation paths resolve to `dimdoors:none`.

## Rift Data

Rift data lives in:

```text
data/<namespace>/pockets/rift_data/<path>.json
```

It defines the destination and link behavior applied to rift block entities.

Fields:

| Field | Required | Type | Default | Meaning |
| --- | --- | --- | --- | --- |
| `destination` | No | Virtual target object | `dimdoors:none` | What the rift does when used. |
| `properties` | No | Link properties object | None | Link weighting and one-way behavior. |
| `color` | No | RGBA object | `{red:0,green:0,blue:0,alpha:0}` | Rift color. |
| `alwaysDelete` | No | Boolean | `false` | Whether the rift should always delete. |
| `forcedColor` | No | Boolean | `false` | Whether color is forced. |

Example:

```json
{
  "destination": {
    "type": "dimdoors:public_pocket"
  }
}
```

### Link Properties

`properties` fields:

| Field | Required | Type | Default | Meaning |
| --- | --- | --- | --- | --- |
| `floatingWeight` | No | Float | `0.0` | Weight when the rift is detached/floating. |
| `entranceWeight` | No | Float | `0.0` | Weight when the rift is an entrance. |
| `groups` | No | Array of integers | `[]` | Link groups used by random-link targets. |
| `linksRemaining` | No | Integer | `0` | Number of times this rift can be selected as a link. |
| `oneWay` | No | Boolean | `false` | If true, generated return links are suppressed. |

Example:

```json
{
  "destination": {
    "type": "dimdoors:dungeon",
    "dungeonGroup": "dimdoors:gold",
    "newRiftWeight": 1,
    "weightMaximum": 100,
    "coordFactor": 1,
    "positiveDepthFactor": 10000,
    "negativeDepthFactor": 160,
    "acceptedGroups": [0],
    "noLink": false,
    "noLinkBack": false
  },
  "properties": {
    "groups": [0, 1],
    "linksRemaining": 1
  }
}
```

### RGBA

`color` fields:

| Field | Type |
| --- | --- |
| `red` | Float |
| `green` | Float |
| `blue` | Float |
| `alpha` | Float |

Example:

```json
{
  "color": {
    "red": 1.0,
    "green": 0.0,
    "blue": 0.0,
    "alpha": 1.0
  },
  "forcedColor": true
}
```

## Virtual Targets

Virtual targets describe what a rift does when something uses it. They appear inside `pockets/rift_data`, `door/data`, and nested fields such as `wrappedDestination`, `ifDestination`, and `otherwiseDestination`.

Virtual targets are not the same as virtual pockets:

| Concept | JSON location | Purpose |
| --- | --- | --- |
| Virtual target | Inside rift data or door data. | Destination behavior for an existing rift. |
| Virtual pocket | `pockets/virtual` or `pockets/groups`. | Selector for a generator that creates a pocket. |

Some virtual targets generate pockets, and those targets reference virtual pockets.

All virtual target objects use `type`:

```json
{
  "type": "dimdoors:escape",
  "canEscapeLimbo": true
}
```

Supported target types:

| Type | Fields | Purpose |
| --- | --- | --- |
| `dimdoors:none` | No fields. | No destination. |
| `dimdoors:limbo` | No fields. | Sends to Limbo. |
| `dimdoors:unstable` | No fields. | Unstable target behavior. |
| `dimdoors:pocket_exit` | No fields. | Marker replaced with the source link when a pocket is set up. |
| `dimdoors:private` | No fields. | Generates/targets a private pocket. |
| `dimdoors:private_pocket_exit` | No fields. | Private pocket exit target. |
| `dimdoors:escape` | `canEscapeLimbo` | Escapes from pocket dimensions or Limbo. |
| `dimdoors:public_pocket` | `wrappedDestination` | Generates a public pocket. |
| `dimdoors:template` | `template`, `wrappedDestination` | Generates a specific virtual pocket. |
| `dimdoors:pocket_entrance` | `weight`, `ifDestination`, `otherwiseDestination` | Entrance marker consumed during pocket setup. |
| `dimdoors:available_link` | Random-target fields | Links to existing/new rifts. |
| `dimdoors:dungeon` | Random-target fields plus `dungeonGroup` | Generates or links to dungeon pockets. |
| `dimdoors:id_marker` | `id` | Marker used by generated door/rift setup. |
| `dimdoors:rift_reference` | `target` | Points to an absolute rift location. |
| `dimdoors:global` | `target` | Same codec as `rift_reference`. |
| `dimdoors:local` | `target` | Deprecated local block-position reference. |
| `dimdoors:relative` | `offset` | Deprecated relative block-position reference. |

### Location Object

Targets that use `target` as an absolute location use:

```json
{
  "world": "minecraft:overworld",
  "pos": [0, 64, 0]
}
```

`world` is a dimension ID. `pos` is a three-integer block position array:

```text
[x, y, z]
```

Targets that use a local block position or vector also use three-integer arrays.

### No-Field Targets

These targets only require `type`:

| Type | Runtime behavior |
| --- | --- |
| `dimdoors:none` | No destination. Entity use fails as unlinked behavior. |
| `dimdoors:limbo` | Sends entities to Limbo near their current X/Z, at Y 255. |
| `dimdoors:unstable` | Randomly chooses between a dungeon-style target and Limbo. |
| `dimdoors:pocket_exit` | Marker replaced during pocket setup with the incoming link target. |
| `dimdoors:private` | Sends the owning player or owned entity to that owner's private pocket, generating one if needed. |
| `dimdoors:private_pocket_exit` | Sends the owning player or owned entity back to their last private-pocket exit, or to Limbo if no valid exit exists. |

Examples:

```json
{
  "type": "dimdoors:limbo"
}
```

```json
{
  "type": "dimdoors:private"
}
```

`dimdoors:pocket_exit` is a setup marker. It should normally be nested in a `dimdoors:pocket_entrance` marker rather than used as a final player-facing rift destination:

```json
{
  "type": "dimdoors:pocket_exit"
}
```

During final pocket setup, after at least one `dimdoors:pocket_entrance` marker is found, every remaining `dimdoors:pocket_exit` marker is replaced with the source link target passed into pocket generation. If the rift's properties are one-way, the exit becomes `dimdoors:none`.

### Random-Target Fields

`dimdoors:available_link` and `dimdoors:dungeon` share these required fields:

| Field | Type | Meaning |
| --- | --- | --- |
| `newRiftWeight` | Float | Weight for creating a new rift/pocket instead of selecting an existing one. |
| `weightMaximum` | Double | Distance-weight curve maximum. |
| `coordFactor` | Double | X/Z coordinate distance factor. |
| `positiveDepthFactor` | Double | Depth factor when moving deeper. |
| `negativeDepthFactor` | Double | Depth factor when moving shallower. |
| `acceptedGroups` | Array of integers | Link groups this target may use. |
| `noLink` | Boolean | If true, do not link from source to target. |
| `noLinkBack` | Boolean | If true, do not link from target back to source. |

Random targets can choose an existing registered rift or create a new rift/pocket.

An existing rift can be selected only when all of these are true:

| Requirement | Meaning |
| --- | --- |
| The target rift has link properties. | Rifts with no `properties` are skipped. |
| The target rift has nonzero weight. | Detached rifts use `floatingWeight`; entrance rifts use `entranceWeight`. |
| `acceptedGroups` intersects the target rift's `groups`. | At least one integer group must match. |
| `linksRemaining` is not `0`. | A target with no remaining links is skipped. |

`newRiftWeight` controls whether creating a new target is part of the weighted choice:

| Value | Behavior |
| --- | --- |
| Greater than `0` | Adds "create a new target" as a weighted option. |
| `0` | Does not create a new target as a weighted option. If no existing links match, the target fails. |
| `-1` | Creates a new target only when no existing links match. |

`weightMaximum`, `coordFactor`, `positiveDepthFactor`, and `negativeDepthFactor` shape the distance weighting. Larger coordinate or depth factors make rifts farther away in that axis less likely to be selected. For newly generated locations, positive and negative depth offsets are divided by the corresponding depth factor.

`dimdoors:dungeon` additionally requires:

| Field | Type | Meaning |
| --- | --- | --- |
| `dungeonGroup` | Pocket group ID | Group used when generating a new dungeon pocket. |

Example from the default pack:

```json
{
  "type": "dimdoors:available_link",
  "newRiftWeight": 1,
  "weightMaximum": 100,
  "coordFactor": 1,
  "positiveDepthFactor": 80,
  "negativeDepthFactor": 10000,
  "acceptedGroups": [0],
  "noLink": false,
  "noLinkBack": false
}
```

`dimdoors:dungeon` example:

```json
{
  "type": "dimdoors:dungeon",
  "dungeonGroup": "dimdoors:gold",
  "newRiftWeight": 1,
  "weightMaximum": 100,
  "coordFactor": 1,
  "positiveDepthFactor": 10000,
  "negativeDepthFactor": 160,
  "acceptedGroups": [0],
  "noLink": false,
  "noLinkBack": false
}
```

`noLink` and `noLinkBack` affect only the links created by this target:

| Field | When `false` | When `true` |
| --- | --- | --- |
| `noLink` | Source rift is linked to the selected/generated target. | Source rift is not rewritten to point at the target. |
| `noLinkBack` | Selected/generated target is linked back to the source, unless the target rift has `oneWay`. | Target is not linked back to the source. |

### `dimdoors:available_link`

Fields: all random-target fields.

This target searches for a compatible existing rift. When a new target is created, it uses the default dungeon generation path.

Example from `pockets/rift_data/default_dungeon.json`:

```json
{
  "type": "dimdoors:available_link",
  "newRiftWeight": 1,
  "weightMaximum": 100,
  "coordFactor": 1,
  "positiveDepthFactor": 80,
  "negativeDepthFactor": 10000,
  "acceptedGroups": [0],
  "noLink": false,
  "noLinkBack": false
}
```

### `dimdoors:dungeon`

Fields: all random-target fields, plus `dungeonGroup`.

This target behaves like `dimdoors:available_link`, but newly generated dungeon pockets use the named group.

Example:

```json
{
  "type": "dimdoors:dungeon",
  "dungeonGroup": "dimdoors:myth",
  "newRiftWeight": 1,
  "weightMaximum": 100,
  "coordFactor": 1,
  "positiveDepthFactor": 80,
  "negativeDepthFactor": 10000,
  "acceptedGroups": [0],
  "noLink": false,
  "noLinkBack": false
}
```

### `dimdoors:pocket_entrance`

Fields:

| Field | Required | Type | Default | Meaning |
| --- | --- | --- | --- | --- |
| `weight` | Yes | Float | None | Weight for choosing this marker as the generated pocket entrance. |
| `ifDestination` | No | Virtual target | `dimdoors:none` | Destination assigned to the selected entrance marker. |
| `otherwiseDestination` | No | Virtual target | `dimdoors:none` | Destination assigned to unselected entrance markers. |

During pocket setup, one entrance marker in the pocket is selected by weight. The selected marker is registered as the pocket entrance and becomes `ifDestination`; other entrance markers become `otherwiseDestination`.

Example:

```json
{
  "type": "dimdoors:pocket_entrance",
  "weight": 1.0,
  "ifDestination": {
    "type": "dimdoors:pocket_exit"
  }
}
```

Default datapack reference:

```json
{
  "destination": {
    "type": "dimdoors:pocket_entrance",
    "weight": 1.0,
    "ifDestination": {
      "type": "dimdoors:pocket_exit"
    }
  }
}
```

### `dimdoors:public_pocket`

Fields:

| Field | Required | Type | Default | Meaning |
| --- | --- | --- | --- | --- |
| `wrappedDestination` | No | Virtual target | `dimdoors:none` | Existing generated target. Usually omitted in datapack JSON. |

This target creates or reuses a public pocket one depth deeper than the source rift's virtual location.

When `wrappedDestination` is `dimdoors:none`, first use generates a public pocket and replaces the wrapped destination with a rift reference to that generated pocket's entrance. When `wrappedDestination` already points somewhere valid, the target uses that existing wrapped destination.

Example:

```json
{
  "type": "dimdoors:public_pocket"
}
```

Example with explicit wrapped destination:

```json
{
  "type": "dimdoors:public_pocket",
  "wrappedDestination": {
    "type": "dimdoors:rift_reference",
    "target": {
      "world": "dimdoors:dungeon",
      "pos": [0, 64, 0]
    }
  }
}
```

### `dimdoors:template`

Fields:

| Field | Required | Type | Default | Meaning |
| --- | --- | --- | --- | --- |
| `template` | Yes | Virtual pocket holder | None | Virtual pocket used to generate the pocket. |
| `wrappedDestination` | No | Virtual target | `dimdoors:none` | Existing generated target. Usually omitted in datapack JSON. |

This target creates or reuses a pocket from the named virtual pocket. New pockets are generated one depth deeper than the source rift's virtual location.

`template` points to `pockets/virtual` content. Dungeon group targets use `dungeonGroup` instead.

Example:

```json
{
  "type": "dimdoors:template",
  "template": "dimdoors:lab/labfinal"
}
```

Example with a nested wrapped destination:

```json
{
  "type": "dimdoors:template",
  "template": "dimdoors:lab/labfinal",
  "wrappedDestination": {
    "type": "dimdoors:none"
  }
}
```

### `dimdoors:escape`

Fields:

| Field | Required | Type | Meaning |
| --- | --- | --- | --- |
| `canEscapeLimbo` | Yes | Boolean | Whether this target can escape from Limbo as well as pocket dimensions. |

The escape target only works from pocket dimensions or Limbo. For players, it tries configured bed spawn/world spawn behavior and then randomizes the final return location using the Limbo config. Non-player entities currently fail this target.

Example:

```json
{
  "type": "dimdoors:escape",
  "canEscapeLimbo": true
}
```

### `dimdoors:id_marker`

Fields:

| Field | Required | Type | Meaning |
| --- | --- | --- | --- |
| `id` | Yes | Integer | Rift ID exposed to generator modifiers. |

This is a marker used by schematic rifts and generated door rifts so modifiers can find them. A rift with this target is added to the `RiftManager` under its integer `id`.

Example:

```json
{
  "type": "dimdoors:id_marker",
  "id": 4
}
```

Leaving an `id_marker` as the final destination is usually a broken setup state. In game, using that rift only reports the marker ID.

### `dimdoors:rift_reference`

Fields:

| Field | Required | Type | Meaning |
| --- | --- | --- | --- |
| `target` | Yes | Location object | Absolute rift or target location. |

This target points to a specific block position in a specific dimension. The target resolver checks the exact position, then the block below, then the block above. It can resolve either a target block entity or a dimensional-door block target.

Example:

```json
{
  "type": "dimdoors:rift_reference",
  "target": {
    "world": "minecraft:overworld",
    "pos": [100, 64, -20]
  }
}
```

When a rift reference is registered, the rift registry tracks that source-to-target link. If the referenced rift is deleted, this target can be invalidated.

### `dimdoors:global`

Fields:

| Field | Required | Type | Meaning |
| --- | --- | --- | --- |
| `target` | Yes | Location object | Absolute rift or target location. |

`dimdoors:global` uses the same JSON shape as `dimdoors:rift_reference`.

Example:

```json
{
  "type": "dimdoors:global",
  "target": {
    "world": "minecraft:overworld",
    "pos": [0, 64, 0]
  }
}
```

New JSON should use `dimdoors:rift_reference`. `dimdoors:global` exists as a compatibility name.

### `dimdoors:local`

Deprecated. Kept for older data migration.

Fields:

| Field | Required | Type | Meaning |
| --- | --- | --- | --- |
| `target` | Yes | Block position array | Position in the same dimension as the source rift. |

Example:

```json
{
  "type": "dimdoors:local",
  "target": [0, 64, 0]
}
```

The runtime target location is:

```text
world = source_rift_world
pos = target
```

Use `dimdoors:rift_reference` for new absolute links, or `dimdoors:relative` modifier for linking rifts inside a generated pocket.

### `dimdoors:relative`

Deprecated. Kept for older data migration.

Fields:

| Field | Required | Type | Meaning |
| --- | --- | --- | --- |
| `offset` | Yes | Vec3i array | Offset from the source rift position. |

Example:

```json
{
  "type": "dimdoors:relative",
  "offset": [0, 0, 1]
}
```

The runtime target location is:

```text
source_rift_position + offset
```

For generated pocket JSON, use the `dimdoors:relative` modifier when the goal is to link two ID-marked rifts in the same generated pocket.

## Door Data

Door data lives in:

```text
data/<namespace>/door/data/<door_item_path>.json
```

The file ID should match the door item ID that should receive the data.

Example:

```text
data/minecraft/door/data/iron_door.json
```

applies to:

```text
minecraft:iron_door
```

The root JSON is an array of entries. The first entry whose `condition` matches the placed entrance rift is used.

Example:

```json
[
  {
    "condition": {
      "type": "dimdoors:always_true"
    },
    "data": {
      "destination": {
        "type": "dimdoors:public_pocket"
      }
    }
  }
]
```

Entry fields:

| Field | Required | Type | Meaning |
| --- | --- | --- | --- |
| `condition` | Yes | Door condition object | Determines whether this entry applies. |
| `data` | Yes | Door rift data object | Destination and optional link properties. |

`data` fields:

| Field | Required | Type | Meaning |
| --- | --- | --- | --- |
| `destination` | Yes | Virtual target object | Destination assigned to the door's entrance rift. |
| `properties` | No | Link properties object | Optional link behavior. |

Door data uses the same virtual target and link properties formats documented above.

### Door Conditions

All conditions use `type`.

Supported conditions:

| Type | Fields | Meaning |
| --- | --- | --- |
| `dimdoors:always_true` | No fields. | Always matches. |
| `dimdoors:all` | `conditions` | Matches when every child condition matches. |
| `dimdoors:any` | `conditions` | Matches when any child condition matches. |
| `dimdoors:inverse` | `condition` | Negates a child condition. |
| `dimdoors:world_match` | `world` | Matches the rift's dimension. |
| `dimdoors:biome` | `biome` | Matches the biome at the rift position. |
| `dimdoors:height` | `height` | Equation evaluated with variable `height`. |
| `dimdoors:waterlogged` | `waterlogged` | Matches the block state's waterlogged property. |
| `dimdoors:light_level` | `light_level` | Equation evaluated with variable `light`. |

Examples:

```json
{
  "type": "dimdoors:world_match",
  "world": "minecraft:overworld"
}
```

```json
{
  "type": "dimdoors:height",
  "height": "height > 60"
}
```

```json
{
  "type": "dimdoors:all",
  "conditions": [
    {
      "type": "dimdoors:world_match",
      "world": "minecraft:overworld"
    },
    {
      "type": "dimdoors:height",
      "height": "height > 60"
    }
  ]
}
```

## Complete Minimal Custom Pack Example

```text
my_pack/
  pack.mcmeta
  data/
    example/
      pockets/
        generators/
          rooms/
            start.json
        virtual/
          rooms/
            start.json
        groups/
          dungeon.json
        rift_data/
          entrance.json
        schematic/
          rooms/
            start.schem
```

`data/example/pockets/generators/rooms/start.json`:

```json
{
  "type": "dimdoors:schematic",
  "id": "example:rooms/start",
  "modifiers": [
    {
      "type": "dimdoors:rift_data",
      "ids": [0],
      "rift_data": "example:entrance"
    }
  ],
  "tags": ["example"]
}
```

`data/example/pockets/virtual/rooms/start.json`:

```json
{
  "type": "dimdoors:id",
  "id": "example:rooms/start"
}
```

`data/example/pockets/groups/dungeon.json`:

```json
{
  "type": "dimdoors:tag",
  "required": ["example"]
}
```

`data/example/pockets/rift_data/entrance.json`:

```json
{
  "destination": {
    "type": "dimdoors:pocket_entrance",
    "weight": 1.0,
    "ifDestination": {
      "type": "dimdoors:pocket_exit"
    }
  }
}
```

The generator expects `data/example/pockets/schematic/rooms/start.schem` to exist.

## Common Mistakes

| Problem | Fix |
| --- | --- |
| Missing `modifiers` on a generator. | Add `"modifiers": []` if there are no modifiers. |
| Confusing generator ID with schematic ID. | The generator file path defines the generator ID; the schematic generator `id` field points to a `.schem`. |
| Using `blacklist` instead of `blackList`. | The tag selector field is exactly `blackList`. |
| Expecting top-level `offset_x`/`offset_y`/`offset_z` to move a pocket. | Those are legacy ignored fields. Use a `dimdoors:offset` modifier with `offsetX`, `offsetY`, `offsetZ`. |
| Using `template_id` in a template modifier. | The field is `templateId`. |
| Using decimal JSON numbers in equation fields. | Use strings for equations, especially when the value is not an integer. |
| Referencing a missing schematic. | Put the `.schem` under `data/<namespace>/pockets/schematic/` and reference `<namespace>:<path>`. |
| Forgetting namespaces. | Use explicit IDs like `dimdoors:pocket_entrance` or `example:rooms/start`. |
