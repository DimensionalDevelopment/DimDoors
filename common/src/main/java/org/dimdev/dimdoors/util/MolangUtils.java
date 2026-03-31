package org.dimdev.dimdoors.util;

import com.bedrockk.molang.Expression;
import com.bedrockk.molang.MoLang;
import com.bedrockk.molang.runtime.MoLangRuntime;
import com.bedrockk.molang.runtime.value.MoValue;
import com.mojang.serialization.Codec;

import java.util.Map;

public class MolangUtils {
    public static final MoLangRuntime MAIN_RUNTIME = MoLang.createRuntime();
    public static final Codec<Expression> CODEC = Codec.STRING.xmap(s -> MoLang.createParser(s).parseExpression(), Expression::getOriginalString);
    public static final Expression ONE = MoLang.createParser("1").parseExpression();
    public static final Expression ZERO = MoLang.createParser("0").parseExpression();
    public static final Expression FIVE = MoLang.createParser("5").parseExpression();

    public static boolean evaulateBoolean(Expression expression, Map<String, MoValue> values) {
        return evaulateDouble(expression, values) == 1.0;
    }

    public static double evaulateDouble(Expression expression, Map<String, MoValue> values) {
        return MAIN_RUNTIME.execute(expression, values).asDouble();
    }
}
