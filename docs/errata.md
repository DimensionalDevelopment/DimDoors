# Errata

**What it is for:** anything in the game (a rift, an item, a pocket, a machine) can say "here is my data, edit it", and players get a working in-game editor without anyone building a GUI for it. Editing happens on a copy. Nothing becomes real until the server has checked the edit and applied it.

## The layers

| Layer | What it does | Where |
|---|---|---|
| **Schema** (shape) | Describes the data's structure: fields, types, ranges, defaults, which variants exist | `dimcore/api/schema/nodes`, `SchemaCodec`, `Schemas` |
| **Subject** (translator) | Turns a real object into an NBT value and back, declares its schema, and says whether editing may continue | `ConfigSubject`, `BlockEntitySubject` |
| **Session** (authority) | Holds the last value sent to the client; on apply it checks permissions, conflicts and references, then writes only what changed | `SchemaSession`, `SchemaSessions` |
| **References** (lookup) | Values that point at things only the server can find: lists options, turns typed input into the stored value, labels and validates | `ReferenceSource`, `ReferenceSources` |
| **Client** (view) | Shows the editor and holds unapplied edits; decides nothing | `client/schema`: `ClientSession`, `SchemaClient`, `SchemaImGui` |
| **Elements** (custom widgets) | Replace the default widget for one node; fall back to it when the element isn't available | `ElementNode`, `SchemaElements` |

NBT is the value format inside the whole API: packets, the client's working copy, and the server's diff and checks. The subject's `read`/`write` is the only place it converts to or from the real object.

## Flow

1. **Open:** `SchemaSessions.open(player, subject)`.
   - It refuses if the client can't receive the packets (the player gets a chat message), if the permission check fails, or if `subject.isValid` is false.
   - It sends the resolved schema, the eager reference options, and the current value with its labels and inputs.
2. **Edit:** the client edits its working copy. Switching a dispatch variant builds that variant's default value on the client.
3. **Resolve** (references only): typed input, such as a location, goes to the server. The server's `resolve` turns it into the stored value and `validate` checks it. The client then puts it in the working copy and shows the label.
4. **Apply:**
   - The client sends its whole working copy.
   - The server finds which paths changed since the last value it sent.
   - For each changed path, it checks `canWrite`, then whether the server's value changed in the meantime (a conflict, unless the field is `overridable` and `canOverride` passes), then validates any references.
   - It calls `subject.write(value, paths)` with the paths that passed, reads the subject again, and sends back the new value plus errors.
5. **Close:** the player closes the editor. The server also closes it when `isValid` fails, permission is lost, or the player quits or dies.

## Building a schema

### From codecs (the direct way)
- Use `Schemas` leaves where plain codecs would go:
  - `BOOL`, `INT`, `FLOAT`, `DOUBLE`, `STRING`;
  - `int(min, max)` and `float(min, max)` (clamped when decoded);
  - `registryKey(key)`, `holder(key, codec)` and `reference(source, codec)`, which become reference fields;
  - `.setOf()` / `.listOf()`.
- `fieldOf`, `optionalFieldOf` and `xmap` keep the schema attached. `MapCodec.schema(id)` reads the struct out of any map codec. Fields still on plain codecs show up as untyped.
- `Schemas.record(id) { … }` works like `RecordCodecBuilder` and returns a codec that carries its schema.
- `Schemas.dispatch(registry, codec, default)` attaches a dispatch and registers the registry's variant table. DimDoors: `VirtualTarget.CODEC`.
- **Field flags:** `.readOnly()` (shown, never written), `.label(key)`, `.overridable()`.
- **Presence comes from the codec:** `fieldOf` is required, `optionalFieldOf(name, default)` is defaulted, `optionalFieldOf(name)` is nullable, which shows a "set" checkbox.

### By hand
A subject can build `StructNode`/`FieldNode` directly and handle `read`/`write` itself. Codecs are just the shortcut. `RiftConfigSubject` builds its record fields this way.

### Labels
Keys are generated as `schema.<namespace>.<struct path>.<field>`; dispatch variants use `schema.<namespace>.<type path>`. A field's `label` overrides its key.

## Writing a subject
```kotlin
class HeldItemSubject(private val hand: InteractionHand, private val item: Item) : ConfigSubject {
    override val title = item.description
    override fun schema(player: ServerPlayer) = KeyData.CODEC.schema(id("key_data"))
    override fun read(player: ServerPlayer) = KeyData.CODEC.encodeStart(ops(player), player.getItemInHand(hand).keyData()).orThrow as CompoundTag
    override fun write(player: ServerPlayer, value: CompoundTag, paths: List<List<String>>) =
        KeyData.CODEC.parse(ops(player), value).map { player.getItemInHand(hand).setKeyData(it) }
    override fun isValid(player: ServerPlayer) = player.getItemInHand(hand).`is`(item)
}
```
- `write` receives only the paths that passed every check, so decode everything first and apply only those paths.
- Use registry-aware ops (`registryAccess().createSerializationContext(NbtOps.INSTANCE)`), so `Holder` fields encode as ids.
- `BlockEntitySubject` already checks that it's the same block entity, in the same level, within vanilla reach, and that the opening item is still in hand.

## Writing a reference source
Implement `ReferenceSource`:
- `options` for eager lists;
- `input` (stored value → editable form) and `resolve` (editable form → stored value + label) for typed entry;
- `describe` and `validate`.

Register it by passing it to `Schemas.reference(source, codec)`. Registry references (`registryKey`/`holder`) get a built-in `RegistrySource`.
DimDoors: `rift/VertexReferenceSource` stores a rift `Vertex` and is edited as a `Location` (`{world, pos: [x, y, z]}`).

## Writing an element
- **Server:** `codec.element(id, config)` wraps the node.
- **Client:** `SchemaElements.register(id) { ctx -> … }`, inside your imguimc-loaded block.
- The context gives `label`, `path`, `value`, `config`, the inner `node`, and `fallback()`. Return a new tag, or `null` for no change.
- A client without the element renders the inner node normally.
- Built in: `dimcore:color` (`ColorElement`). DimDoors: `dials`, `location`, `rotation`.

## Permissions
`SchemaPermissions.hook` has `canOpen`, `canWrite(path)` and `canOverride(path)`, all defaulting to permission level 2. Replace the hook to plug in a permission mod.

## Packets (dimcore namespace)
- S2C: `session_open`, `session_applied`, `session_resolved`, `session_closed`.
- C2S: `session_apply`, `session_resolve`, `session_close`.

Sends are skipped for clients that don't have the channel. On NeoForge the channels are optional, so clients without dimcore can still connect.

## Requirements
- **The client needs dimcore** to open an editor at all; otherwise the player just gets a chat message.
- **The editor itself needs imguimc.** Without it, a plain screen says so.
- **Wiring:** call `SchemaSessions.init()` and `SchemaSessions.registerPackets(register)` once (DimDoors does both in `DimensionalDoors`).

## DimDoors usage
- **Rift Configuration Tool:** right-click a rift to open a `RiftConfigSubject`, via `RiftBlockEntity.configSubject`.
- **`RiftConfigSubject`:** edits `RiftData` (minus `properties` and the legacy `forcedColor`) plus each block entity's `configRecords`. The destination goes through `setDestination`.
- **Custom pieces:** `VertexReferenceSource` and `LocationElement` handle `rift_reference` targets; `DialsElement` and `RotationElement` handle dialing addresses and rift rotation.
