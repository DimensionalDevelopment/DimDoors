package org.dimdev.dimdoors.client.config;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;

final class CollectionElementTypes {
    private CollectionElementTypes() {
    }

    static TypeToken<?> resolve(OptionInfo<? extends Collection> option) {
        TypeToken<?> declared = resolveDeclared(option.typeToken());

        if (declared != null) {
            return declared;
        }

        Collection<?> collection = option.value();

        if (collection != null) {
            for (Object entry : collection) {
                if (entry != null) {
                    return TypeToken.of(entry.getClass());
                }
            }
        }

        throw new IllegalStateException("Cannot determine collection element type for " + option.key());
    }

    private static TypeToken<?> resolveDeclared(TypeToken<?> collectionType) {
        if (!Collection.class.isAssignableFrom(collectionType.getRawType())) {
            throw new IllegalArgumentException("Not a collection type: " + collectionType);
        }

        TypeToken<?> elementType = collectionType.resolveType(Collection.class.getTypeParameters()[0]);

        if (containsUnresolvedType(elementType.getType())) {
            return null;
        }

        return elementType;
    }

    private static boolean containsUnresolvedType(Type type) {
        if (type instanceof TypeVariable<?>) {
            return true;
        }

        if (type instanceof WildcardType wildcardType) {
            for (Type upperBound : wildcardType.getUpperBounds()) {
                if (containsUnresolvedType(upperBound)) {
                    return true;
                }
            }

            for (Type lowerBound : wildcardType.getLowerBounds()) {
                if (containsUnresolvedType(lowerBound)) {
                    return true;
                }
            }

            return true;
        }

        if (type instanceof ParameterizedType parameterizedType) {
            for (Type argument : parameterizedType.getActualTypeArguments()) {
                if (containsUnresolvedType(argument)) {
                    return true;
                }
            }

            return false;
        }

        if (type instanceof GenericArrayType genericArrayType) {
            return containsUnresolvedType(genericArrayType.getGenericComponentType());
        }

        return false;
    }
}
