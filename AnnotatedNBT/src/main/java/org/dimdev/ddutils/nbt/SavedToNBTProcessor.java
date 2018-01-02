package org.dimdev.ddutils.nbt;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.*;

@SupportedAnnotationTypes("org.dimdev.ddutils.nbt.SavedToNBT")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SavedToNBTProcessor extends AbstractProcessor {

    private static final String BOOLEAN = "java.lang.Boolean";
    private static final String BYTE = "java.lang.Byte";
    private static final String SHORT = "java.lang.Short";
    private static final String INTEGER = "java.lang.Integer";
    private static final String LONG = "java.lang.Long";
    private static final String CHAR = "java.lang.Char";
    private static final String FLOAT = "java.lang.Float";
    private static final String DOUBLE = "java.lang.Double";
    private static final String STRING = "java.lang.String";
    private static final String COLLECTION = "java.util.Collection"; // TODO: Allow storing any Iterable?
    private static final String MAP = "java.util.Map";
    private static final String MAP_ENTRY = "java.util.Map.Entry";
    private static final String NBT_STORABLE = "org.dimdev.ddutils.nbt.INBTStorable";
    private static final String VEC_3I = "net.minecraft.util.math.Vec3i";
    private static final String LOCATION = "org.dimdev.ddutils.Location";
    private static final String RGBA = "org.dimdev.ddutils.RGBA";
    private static final String VIRTUAL_LOCATION = "org.dimdev.dimdoors.shared.VirtualLocation";
    private static final String UUID = "java.util.UUID";

    private Map<String, Integer> varCounter = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) { // TODO: generics, inheritance, different exception for error type
        roundEnv.getElementsAnnotatedWith(SavedToNBT.class);
        for (Element element : roundEnv.getElementsAnnotatedWith(SavedToNBT.class)) {
            if (element.getKind() != ElementKind.CLASS) continue;

            TypeElement classElement = (TypeElement) element;
            Element enclosingElement = classElement;
            while (!(enclosingElement instanceof PackageElement)) {
                enclosingElement = enclosingElement.getEnclosingElement();
            }
            PackageElement packageElement = (PackageElement) enclosingElement;
            JavaFileObject jfo;
            IndentedPrintWriter w;
            try {
                jfo = processingEnv.getFiler().createSourceFile(packageElement.getQualifiedName() + "."+ classElement.getSimpleName() + "NBTWriter");
                w = new IndentedPrintWriter(jfo.openWriter());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String type = classElement.getSimpleName().toString();

            List<VariableElement> savedFields = new ArrayList<>();
            for (Element enclosedElement : classElement.getEnclosedElements()) {
                if (enclosedElement.getKind() == ElementKind.FIELD && enclosedElement.getAnnotationsByType(SavedToNBT.class).length > 0) {
                    savedFields.add((VariableElement) enclosedElement);
                }
            }

            w.println("package " + packageElement.getQualifiedName() + ";");
            w.println();
            w.println("import net.minecraft.nbt.*;");
            w.println();
            w.println("public final class " + type + "NBTWriter {");
            w.println();
            w.indent();
            w.print("public static void writeToNBT(" + classElement.getQualifiedName() + " obj, NBTTagCompound nbt) {");
            w.indent();
            for (VariableElement field : savedFields) {
                w.println();
                writeFieldWriteCode(w, field.asType(), field.getSimpleName().toString());
            }
            varCounter.clear();
            if (savedFields.size() == 0) w.println();
            w.unindent();
            w.println("}");
            w.println();
            w.println("@SuppressWarnings({\"OverlyStrongTypeCast\", \"RedundantSuppression\"})"); // We want the cast to fail if it's ther wrong type
            w.print("public static void readFromNBT(" + classElement.getQualifiedName() + " obj, NBTTagCompound nbt) {");
            w.indent();
            for (VariableElement field : savedFields) {
                w.println();
                writeFieldReadCode(w, field.asType(), field.getSimpleName().toString());
            }
            varCounter.clear();
            if (savedFields.size() == 0) w.println();
            w.unindent();
            w.println("}");
            w.unindent();
            w.println("}");
            w.close();
        }
        return true;
    }

    // Writing

    // This tries to write using a .set method, and creates and fills a tag only if necessary
    private void writeFieldWriteCode(IndentedPrintWriter w, TypeMirror type, String name) { // TODO: store boxed primitives and boxed primitive collections as primitives
        w.println("// Write field " + type + " " + name);
        switch (type.getKind()) {
            case BOOLEAN:
                w.println("nbt.setBoolean(\"" + name + "\", obj." + name + ");");
                break;
            case BYTE:
                w.println("nbt.setByte(\"" + name + "\", obj." + name + ");");
                break;
            case SHORT:
                w.println("nbt.setShort(\"" + name + "\", obj." + name + ");");
                break;
            case INT:
                w.println("nbt.setInteger(\"" + name + "\", obj." + name + ");");
                break;
            case LONG:
                w.println("nbt.setLong(\"" + name + "\", obj." + name + ");");
                break;
            case CHAR:
                w.println("nbt.setInteger(\"" + name + "\", (int) obj." + name + ");"); // TODO: use short?
                break;
            case FLOAT:
                w.println("nbt.setFloat(\"" + name + "\", obj." + name + ");");
                break;
            case DOUBLE:
                w.println("nbt.setDouble(\"" + name + "\", obj." + name + ");");
                break;
            case ARRAY:
                TypeMirror componentType = ((ArrayType) type).getComponentType();
                if (componentType.getKind() == TypeKind.BYTE) { // TODO: store boolean array as byte array
                    w.println("nbt.setByteArray(\"" + name + "\", obj." + name + ");");
                } else if (componentType.getKind() == TypeKind.INT) {
                    w.println("nbt.setIntArray(\"" + name + "\", obj." + name + ");");
                } else {
                    makeNBTObject(w, type, "obj." + name, newVar("tag")); // TODO: name should depend on field name
                    w.println("nbt.setTag(\"" + name + "\", " + var("tag") + ");");
                }
                break;
            case DECLARED:
                DeclaredType declaredType = (DeclaredType) type;
                Types tu = processingEnv.getTypeUtils();
                Elements eu = processingEnv.getElementUtils();
                switch (declaredType.toString()) {
                    // <editor-fold> TODO: less code duplication
                    case BOOLEAN:
                        w.println("nbt.setBoolean(\"" + name + "\", obj." + name + ");");
                        break;
                    case BYTE:
                        w.println("nbt.setByte(\"" + name + "\", obj." + name + ");");
                        break;
                    case SHORT:
                        w.println("nbt.setShort(\"" + name + "\", obj." + name + ");");
                        break;
                    case INTEGER:
                        w.println("nbt.setInteger(\"" + name + "\", obj." + name + ");");
                        break;
                    case LONG:
                        w.println("nbt.setLong(\"" + name + "\", obj." + name + ");");
                        break;
                    case CHAR:
                        w.println("nbt.setInteger(\"" + name + "\", (int) obj." + name + ");"); // TODO: use short?
                        break;
                    case FLOAT:
                        w.println("nbt.setFloat(\"" + name + "\", obj." + name + ");");
                        break;
                    case DOUBLE:
                        w.println("nbt.setDouble(\"" + name + "\", obj." + name + ");");
                        break;
                    // </editor-fold>
                    case STRING:
                        w.println("nbt.setString(\"" + name + "\", obj." + name + ");");
                        break;
                    case UUID:
                        w.println("nbt.setUUID(\"" + name + "\", obj." + name + ");");
                        break;
                    default:
                        w.println("if (obj." + name + " != null) {");
                        w.indent();
                        if (tu.isAssignable(type, eu.getTypeElement(NBT_STORABLE).asType())) {
                            w.println("if (obj." + name + " != null) nbt.setTag(\"" + name + "\", obj." + name + ".writeToNBT(new NBTTagCompound()));");
                        } else {
                            makeNBTObject(w, type, "obj." + name, newVar("tag")); // TODO: name should depend on field name
                            w.println("nbt.setTag(\"" + name + "\", " + var("tag") + ");");
                        }
                        w.unindent();
                        w.println("}");
                        break;
                }
                break;
            default:
                makeNBTObject(w, type, "obj." + name, newVar("tag")); // TODO: name should depend on field name
                w.println("nbt.setTag(\"" + name + "\", " + var("tag") + ");");
                break;
        }
    }

    private void makeNBTObject(IndentedPrintWriter w, TypeMirror type, String from, String nbt) {
        switch (type.getKind()) {
            case BOOLEAN:
                w.println("NBTTagByte " + nbt + " = new NBTTagByte((byte) (" + from + " ? 1 : 0));");
                break;
            case BYTE:
                w.println("NBTTagByte " + nbt + " = new NBTTagByte(" + from + ");");
                break;
            case SHORT:
                w.println("NBTTagShort " + nbt + " = new NBTTagShort(" + from + ");");
                break;
            case INT:
                w.println("NBTTagInt " + nbt + " = new NBTTagInt(" + from + ");");
                break;
            case LONG:
                w.println("NBTTagLong " + nbt + " = new NBTTagLong(" + from + ");");
                break;
            case CHAR:
                w.println("NBTTagInt " + nbt + " = new NBTTagInt((int) " + from + ");");
                break;
            case FLOAT:
                w.println("NBTTagFloat " + nbt + " = new NBTTagFloat(" + from + ");");
                break;
            case DOUBLE:
                w.println("NBTTagDouble " + nbt + " = new NBTTagDouble(" + from + ");");
                break;
            case ARRAY:
                TypeMirror componentType = ((ArrayType) type).getComponentType();
                if (componentType.getKind() == TypeKind.BYTE) {
                    w.println("NBTTagByteArray " + nbt + " = new NBTTagByteArray(" + from + ");");
                } else if (componentType.getKind() == TypeKind.INT) {
                    w.println("NBTTagIntArray " + nbt + " = new NBTTagIntArray(" + from + ");");
                } else {
                    writeIterable(w, componentType, from, nbt);
                }
            break;
            case DECLARED:
                DeclaredType declaredType = (DeclaredType) type;
                switch (declaredType.toString()) {
                    // <editor-fold> TODO: less code duplication
                    case BOOLEAN:
                        w.println("NBTTagByte " + nbt + " = new NBTTagByte((byte) (" + from + " ? 1 : 0));");
                        break;
                    case BYTE:
                        w.println("NBTTagByte " + nbt + " = new NBTTagByte(" + from + ");");
                        break;
                    case SHORT:
                        w.println("NBTTagShort " + nbt + " = new NBTTagShort(" + from + ");");
                        break;
                    case INTEGER:
                        w.println("NBTTagInt " + nbt + " = new NBTTagInt(" + from + ");");
                        break;
                    case LONG:
                        w.println("NBTTagLong " + nbt + " = new NBTTagLong(" + from + ");");
                        break;
                    case CHAR:
                        w.println("NBTTagInt " + nbt + " = new NBTTagInt((int) " + from + ");");
                        break;
                    case FLOAT:
                        w.println("NBTTagFloat " + nbt + " = new NBTTagFloat(" + from + ");");
                        break;
                    case DOUBLE:
                        w.println("NBTTagDouble " + nbt + " = new NBTTagDouble(" + from + ");");
                        break;
                    // </editor-fold>
                    case STRING:
                        w.println("NBTTagString " + nbt + " = new NBTTagString(" + from + ");");
                        break;
                    default:
                        Types types = processingEnv.getTypeUtils();
                        Elements elements = processingEnv.getElementUtils();
                        if (types.isAssignable(type, types.erasure(elements.getTypeElement(COLLECTION).asType()))) {
                            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
                            if (typeArguments.size() != 1) throw new RuntimeException("Missing type arguments for " + type);
                            TypeMirror elementType = typeArguments.get(0);
                            writeIterable(w, elementType, from, nbt);
                        } else if (types.isAssignable(type, types.erasure(elements.getTypeElement(MAP).asType()))) {
                            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
                            if (typeArguments.size() != 2) throw new RuntimeException("Missing type arguments for " + type);
                            TypeMirror entryType = types.getDeclaredType(elements.getTypeElement(MAP_ENTRY),
                                    typeArguments.get(0), typeArguments.get(1));
                            writeIterable(w, entryType, from + ".entrySet()", nbt);
                        } else if (types.isAssignable(type, types.erasure(elements.getTypeElement(MAP_ENTRY).asType()))) {
                            w.println("NBTTagCompound " + nbt + " = new NBTTagCompound();");
                            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
                            if (typeArguments.size() != 2) throw new RuntimeException("Missing type arguments for " + type);
                            makeNBTObject(w, typeArguments.get(0), from + ".getKey()", newVar("key")); // TODO: configurable key name
                            w.println(nbt + ".setTag(\"" + var("key") + "\", " + var("key") + ");");
                            makeNBTObject(w, typeArguments.get(1), from + ".getValue()", newVar("value"));
                            w.println(nbt + ".setTag(\"" + var("value") + "\", " + var("value") + ");");
                            varGone("key");
                            varGone("value");
                        } else if (types.isAssignable(type, elements.getTypeElement(NBT_STORABLE).asType())) {
                            w.println("NBTTagCompound " + nbt + " = " + from + ".writeToNBT(new NBTTagCompound());");
                        } else if (types.isAssignable(type, elements.getTypeElement(VEC_3I).asType())) {
                            w.println("NBTTagCompound " + nbt + " = new NBTTagCompound();");
                            w.println(nbt + ".setInteger(\"x\", " + from + ".getX());");
                            w.println(nbt + ".setInteger(\"y\", " + from + ".getY());");
                            w.println(nbt + ".setInteger(\"z\", " + from + ".getZ());");
                        } else if (types.isAssignable(type, elements.getTypeElement(LOCATION).asType())) {
                            w.println("NBTTagCompound " + nbt + " = new NBTTagCompound();");
                            w.println(nbt + ".setInteger(\"dim\", " + from + ".getDim());");
                            w.println(nbt + ".setInteger(\"x\", " + from + ".getX());");
                            w.println(nbt + ".setInteger(\"y\", " + from + ".getY());");
                            w.println(nbt + ".setInteger(\"z\", " + from + ".getZ());");
                        } else if (types.isAssignable(type, elements.getTypeElement(RGBA).asType())) {
                            w.println("NBTTagCompound " + nbt + " = new NBTTagCompound();");
                            w.println(nbt + ".setFloat(\"red\", " + from + ".getRed());");
                            w.println(nbt + ".setFloat(\"green\", " + from + ".getGreen());");
                            w.println(nbt + ".setFloat(\"blue\", " + from + ".getBlue());");
                            w.println(nbt + ".setFloat(\"alpha\", " + from + ".getAlpha());");
                        } else if (types.isAssignable(type, elements.getTypeElement(VIRTUAL_LOCATION).asType())) {
                            w.println("NBTTagCompound " + nbt + " = new NBTTagCompound();");
                            w.println(nbt + ".setInteger(\"dim\", " + from + ".getDim());");
                            w.println(nbt + ".setInteger(\"x\", " + from + ".getX());");
                            w.println(nbt + ".setInteger(\"y\", " + from + ".getY());");
                            w.println(nbt + ".setInteger(\"z\", " + from + ".getZ());");
                            w.println(nbt + ".setInteger(\"depth\", " + from + ".getDepth());");
                        } else if (((DeclaredType) type).asElement().getKind() == ElementKind.ENUM) {
                            w.println("NBTTagInt " + nbt + " = new NBTTagInt(" + from + ".ordinal());");
                        } else {
                            throw new RuntimeException("Unsupported type " + type + " for variable " + from + "!");
                        }
                }
                break;
            default:
                throw new RuntimeException("Unsupported type kind " + type + " for variable " + from + "!");
        }
    }

    private void writeIterable(IndentedPrintWriter w, TypeMirror componentType, String from, String nbt) {
        w.println("NBTTagList " + nbt + " = new NBTTagList();");
        w.println("for (" + componentType + " " + newVar("element") + " : " + from + ") {"); // TODO: no java.lang or java.util
        w.indent();
        makeNBTObject(w, componentType, var("element"), newVar("elementNBT")); // TODO: single line if possible
        w.println(nbt + ".appendTag(" + var("elementNBT") + ");");
        varGone("element");
        varGone("elementNBT");
        w.unindent();
        w.println("}");
    }

    // Reading

    private void writeFieldReadCode(IndentedPrintWriter w, TypeMirror type, String name) {
        w.println("// Read field " + type + " " + name);
        switch (type.getKind()) {
            case BOOLEAN:
                w.println("obj." + name + " = nbt.getBoolean(\"" + name + "\");");
                break;
            case BYTE:
                w.println("obj." + name + " = nbt.getByte(\"" + name + "\");");
                break;
            case SHORT:
                w.println("obj." + name + " = nbt.getShort(\"" + name + "\");");
                break;
            case INT:
                w.println("obj." + name + " = nbt.getInteger(\"" + name + "\");");
                break;
            case LONG:
                w.println("obj." + name + " = nbt.getLong(\"" + name + "\");");
                break;
            case CHAR:
                w.println("obj." + name + " = (char) nbt.getInteger(\"" + name + "\");");
                break;
            case FLOAT:
                w.println("obj." + name + " = nbt.getFloat(\"" + name + "\");");
                break;
            case DOUBLE:
                w.println("obj." + name + " = nbt.getDouble(\"" + name + "\");");
                break;
            case ARRAY:
                TypeMirror componentType = ((ArrayType) type).getComponentType();
                if (componentType.getKind() == TypeKind.BYTE) { // TODO: store boolean array as byte array
                    w.println("obj." + name + " = nbt.getByteArray(\"" + name + "\");");
                } else if (componentType.getKind() == TypeKind.INT) {
                    w.println("obj." + name + " = nbt.getIntArray(\"" + name + "\");");
                } else {
                    w.println("NBTBase " + newVar("tag") + " = nbt.getTag(\"" + name + "\");");
                    readNBTObject(w, type, newVar("arr"), var("tag")); // TODO: name should depend on field name
                    w.println("obj." + name + " = " + var("arr") + ";");
                }
                break;
            case DECLARED:
                DeclaredType declaredType = (DeclaredType) type;
                Types tu = processingEnv.getTypeUtils();
                Elements eu = processingEnv.getElementUtils();
                switch (declaredType.toString()) {
                    // <editor-fold> TODO: less code duplication
                    case BOOLEAN:
                        w.println("obj." + name + " = nbt.getBoolean(\"" + name + "\");");
                        break;
                    case BYTE:
                        w.println("obj." + name + " = nbt.getByte(\"" + name + "\");");
                        break;
                    case SHORT:
                        w.println("obj." + name + " = nbt.getShort(\"" + name + "\");");
                        break;
                    case INTEGER:
                        w.println("obj." + name + " = nbt.getInteger(\"" + name + "\");");
                        break;
                    case LONG:
                        w.println("obj." + name + " = nbt.getLong(\"" + name + "\");");
                        break;
                    case CHAR:
                        w.println("obj." + name + " = (char) nbt.getInteger(\"" + name + "\");");
                        break;
                    case FLOAT:
                        w.println("obj." + name + " = nbt.getFloat(\"" + name + "\");");
                        break;
                    case DOUBLE:
                        w.println("obj." + name + " = nbt.getDouble(\"" + name + "\");");
                        break;
                    // </editor-fold>
                    case STRING:
                        w.println("obj." + name + " = nbt.getString(\"" + name + "\");");
                        break;
                    case UUID: // TODO: non top-level UUIDs
                        w.println("obj." + name + " = nbt.getUUID(\"" + name + "\");");
                        break;
                    default:
                        if (tu.isAssignable(type, eu.getTypeElement(NBT_STORABLE).asType())) {
                            w.println("if (nbt.hasKey(\"" + name + "\")) {");
                            w.indent();
                            w.println("obj." + name + " = new " + type + "();");
                            w.println("obj." + name + ".readFromNBT(nbt.getCompoundTag(\"" + name + "\"));");
                            w.unindent();
                            w.println("}");
                        } else {
                            w.println("if (nbt.hasKey(\"" + name + "\")) {");
                            w.indent();
                            w.println("NBTBase " + newVar("tag") + " = nbt.getTag(\"" + name + "\");");
                            readNBTObject(w, type, newVar("arr"), var("tag")); // TODO: name should depend on field name
                            w.println("obj." + name + " = " + var("arr") + ";");
                            w.unindent();
                            w.println("}");
                        }
                        break;
                }
                break;
            default:
                w.println("NBTBase " + newVar("tag") + " = nbt.getTag(\"" + name + "\");");
                readNBTObject(w, type, newVar("arr"), var("tag")); // TODO: name should depend on field name
                w.println("obj." + name + " = " + var("arr") + ";");
                break;
        }
    }

    private void readNBTObject(IndentedPrintWriter w, TypeMirror type, String to, String nbt) {
        switch (type.getKind()) {
            case BOOLEAN:
                w.println(type + " " + to + " = ((NBTTagByte) " + nbt + ").getByte() == 1;");
                break;
            case BYTE:
                w.println(type + " " + to + " = ((NBTTagByte) " + nbt + ").getByte();");
                break;
            case SHORT:
                w.println(type + " " + to + " = ((NBTTagShort) " + nbt + ").getShort();");
                break;
            case INT:
                w.println(type + " " + to + " = ((NBTTagInt) " + nbt + ").getInt();");
                break;
            case LONG:
                w.println(type + " " + to + " = ((NBTTagLong) " + nbt + ").getLong();");
                break;
            case CHAR:
                w.println(type + " " + to + " = (char) ((NBTTagInt) " + nbt + ").getInt();");
                break;
            case FLOAT:
                w.println(type + " " + to + " = ((NBTTagFloat) " + nbt + ").getFloat();");
                break;
            case DOUBLE:
                w.println(type + " " + to + " = ((NBTTagDouble) " + nbt + ").getDouble();");
                break;
            case ARRAY:
                TypeMirror componentType = ((ArrayType) type).getComponentType();
                if (componentType.getKind() == TypeKind.BYTE) {
                    w.println(type + " " + to + " = ((NBTTagByteArray) " + nbt + ").getByteArray();");
                } else if (componentType.getKind() == TypeKind.INT) {
                    w.println(type + " " + to + " = ((NBTTagIntArray) " + nbt + ").getIntArray();");
                } else {
                    if (componentType.getKind() == TypeKind.ARRAY) { // TODO: better array creation
                        w.println(type + " " + to + " = (" + type + ") new Object[((NBTTagList) " + nbt + ").tagCount()];");
                    } else {
                        w.println(type + " " + to + " = new " + componentType + "[((NBTTagList) " + nbt + ").tagCount()];");
                    }
                    String i = newVar("i"); // TODO: this is a workaround for not being able to varGone the variable
                    w.println("int " + i + " = 0;");
                    w.println("for (NBTBase " + newVar("elementNBT") + " : (NBTTagList) " + nbt + ") {");
                    w.indent();
                    readNBTObject(w, componentType, newVar("element"), var("elementNBT")); // TODO: single line if possible
                    w.println(to + "[" + i + "++] = " + var("element") + ";");
                    varGone("element");
                    varGone("elementNBT");
                    w.unindent();
                    w.println("}");
                }
                break;
            case DECLARED:
                DeclaredType declaredType = (DeclaredType) type;
                switch (declaredType.toString()) {
                    // <editor-fold> TODO: less code duplication
                    case BOOLEAN:
                        w.println(type + " " + to + " = ((NBTTagByte) " + nbt + ").getByte() == 1;");
                        break;
                    case BYTE:
                        w.println(type + " " + to + " = ((NBTTagByte) " + nbt + ").getByte();");
                        break;
                    case SHORT:
                        w.println(type + " " + to + " = ((NBTTagShort) " + nbt + ").getShort();");
                        break;
                    case INTEGER:
                        w.println(type + " " + to + " = ((NBTTagInt) " + nbt + ").getInt();");
                        break;
                    case LONG:
                        w.println(type + " " + to + " = ((NBTTagLong) " + nbt + ").getLong();");
                        break;
                    case CHAR:
                        w.println(type + " " + to + " = (char) ((NBTTagInt) " + nbt + ").getInt();");
                        break;
                    case FLOAT:
                        w.println(type + " " + to + " = ((NBTTagFloat) " + nbt + ").getFloat();");
                        break;
                    case DOUBLE:
                        w.println(type + " " + to + " = ((NBTTagDouble) " + nbt + ").getDouble();");
                        break;
                    // </editor-fold>
                    case STRING:
                        w.println(type + " " + to + " = ((NBTTagString) " + nbt + ").getString();");
                        break;
                    default:
                        Types types = processingEnv.getTypeUtils();
                        Elements elements = processingEnv.getElementUtils();
                        if (types.isAssignable(type, types.erasure(elements.getTypeElement(COLLECTION).asType()))) {
                            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
                            if (typeArguments.size() != 1) throw new RuntimeException("Missing type arguments for " + type);
                            TypeMirror elementType = typeArguments.get(0);
                            w.println(type + " " + to + " = " + makeContainer(type) + ";");
                            w.println("for (NBTBase " + newVar("elementNBT") + " : (NBTTagList) " + nbt + ") {");
                            w.indent();
                            readNBTObject(w, elementType, newVar("element"), var("elementNBT")); // TODO: single line if possible
                            w.println(to + ".add(" + var("element") + ");");
                            varGone("element");
                            varGone("elementNBT");
                            w.unindent();
                            w.println("}");
                        } else if (types.isAssignable(type, types.erasure(elements.getTypeElement(MAP).asType()))) {
                            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
                            if (typeArguments.size() != 2) throw new RuntimeException("Missing type arguments for " + type);
                            w.println(type + " " + to + " = " + makeContainer(type) + ";");
                            w.println("for (NBTBase " + newVar("elementNBT") + " : (NBTTagList) " + nbt + ") {");
                            w.indent();
                            readNBTObject(w, typeArguments.get(0), newVar("key"), "((NBTTagCompound) " + var("elementNBT") + ").getTag(\"key\")");
                            readNBTObject(w, typeArguments.get(1), newVar("value"), "((NBTTagCompound) " + var("elementNBT") + ").getTag(\"value\")");
                            w.println(to + ".put(" + var("key") + ", " + var("value") + ");");
                            varGone("key");
                            varGone("value");
                            varGone("elementNBT");
                            w.unindent();
                            w.println("}");
                        } else if (types.isAssignable(type, elements.getTypeElement(NBT_STORABLE).asType())) {
                            w.println(type + " " + to + " = new " + type + "();");
                            w.println(to + ".readFromNBT((NBTTagCompound) " + nbt + ");");
                        } else if (types.isAssignable(type, elements.getTypeElement(VEC_3I).asType())) {
                            w.println(type + " " + to + " = new " + type + "("
                                    + "((NBTTagCompound) "+ nbt + ").getInteger(\"x\"), "
                                    + "((NBTTagCompound) "+ nbt + ").getInteger(\"y\"), "
                                    + "((NBTTagCompound) "+ nbt + ").getInteger(\"z\")" + ");");
                        } else if (types.isAssignable(type, elements.getTypeElement(LOCATION).asType())) {
                            w.println(type + " " + to + " = new " + type + "("
                                      + "((NBTTagCompound) "+ nbt + ").getInteger(\"dim\"), "
                                      + "((NBTTagCompound) "+ nbt + ").getInteger(\"x\"), "
                                      + "((NBTTagCompound) "+ nbt + ").getInteger(\"y\"), "
                                      + "((NBTTagCompound) "+ nbt + ").getInteger(\"z\")" + ");");
                        } else if (types.isAssignable(type, elements.getTypeElement(RGBA).asType())) {
                            w.println(type + " " + to + " = new " + type + "("
                                      + "((NBTTagCompound) "+ nbt + ").getFloat(\"red\"), "
                                      + "((NBTTagCompound) "+ nbt + ").getFloat(\"green\"), "
                                      + "((NBTTagCompound) "+ nbt + ").getFloat(\"blue\"), "
                                      + "((NBTTagCompound) "+ nbt + ").getFloat(\"alpha\")" + ");");
                        } else if (types.isAssignable(type, elements.getTypeElement(VIRTUAL_LOCATION).asType())) {
                            w.println(type + " " + to + " = new " + type + "("
                                    + "((NBTTagCompound) "+ nbt + ").getInteger(\"dim\"), "
                                    + "((NBTTagCompound) "+ nbt + ").getInteger(\"x\"), "
                                    + "((NBTTagCompound) "+ nbt + ").getInteger(\"y\"), "
                                    + "((NBTTagCompound) "+ nbt + ").getInteger(\"z\"), "
                                    + "((NBTTagCompound) "+ nbt + ").getInteger(\"depth\")" + ");");
                        } else if (((DeclaredType) type).asElement().getKind() == ElementKind.ENUM) {
                            w.println(type + " " + to + " = " + type + ".values()[((NBTTagInt) " + nbt + ").getInt()" + "];");
                        } else {
                            throw new RuntimeException("Unsupported type " + type + " for variable " + to + "!");
                        }
                }
                break;
            default:
                throw new RuntimeException("Unsupported type kind for type " + type + " for variable " + to + "!");
        }
    }

    private Object makeContainer(TypeMirror type) {
        Types types = processingEnv.getTypeUtils();
        Elements elements = processingEnv.getElementUtils();
        if (types.isAssignable(types.erasure(elements.getTypeElement("java.util.List").asType()), type)) {
            return "new java.util.ArrayList<>()";
        } else if (types.isAssignable(types.erasure(elements.getTypeElement("java.util.Set").asType()), type)) {
                return "new java.util.HashSet<>()";
        } else if (types.isAssignable(types.erasure(elements.getTypeElement("java.util.Map").asType()), type)) {
            return "new java.util.HashMap<>()";
        } else if (types.isAssignable(types.erasure(elements.getTypeElement("com.google.common.collect.BiMap").asType()), type)) {
            return "com.google.common.collect.HashBiMap.create()";
        } else if (types.isAssignable(types.erasure(elements.getTypeElement("com.google.common.collect.Multiset").asType()), type)) {
            return "com.google.common.collect.HashMultiset.create()";
        } else { // TODO: check that it can be instantiated, don't use generic params
            return "new " + type + "()";
        }
    }

    private String newVar(String name) {
        varCounter.put(name, varCounter.getOrDefault(name, -1) + 1);
        return var(name);
    }

    private void varGone(String name) {
        if (varCounter.get(name) < 0) throw new IllegalStateException("Can't set variable to negative index!");
        varCounter.put(name, varCounter.get(name) - 1);
    }

    private String var(String name) {
        if (varCounter.get(name) == 0) {
            return name;
        } else {
            return name + varCounter.get(name);
        }
    }
}
