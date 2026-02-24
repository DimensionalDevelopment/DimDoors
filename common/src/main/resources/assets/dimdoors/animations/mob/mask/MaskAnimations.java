// Save this class in your mod and generate all required imports

import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

/**
 * Made with Blockbench 5.0.7
 * Exported for Minecraft version 1.19 or later with Mojang mappings
 * @author Author
 */
public class maskAnimation {
	public static final AnimationDefinition idle = AnimationDefinition.Builder.withLength(3.5F).looping()
		.addAnimation("mask", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			position(0.0f, 0.0f, 0.0f, 0.0f),
			position(0.5f, 0.0f, 0.5f, 0.0f),
			position(1.5f, 0.0f, 2.0f, 0.0f),
			position(2.0f, 0.0f, 2.0f, 0.0f),
			position(2.5f, 0.0f, 1.5f, 0.0f),
			position(3.0f, 0.0f, 0.5f, 0.0f),
			position(3.5f, 0.0f, 0.0f, 0.0f)
		))
		.addAnimation("toprightlimb", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, -7.66F, 7.5F), AnimationChannel.Interpolations.LINEAR)
		))
		.build();

    private static Keyframe position(float timestamp, float x, float y, float z) {
        return new Keyframe(timestamp, KeyframeAnimations.degreeVec(x, y, z), AnimationChannel.Interpolations.LINEAR)
    }

    private static Keyframe rotation(float timestamp, float x, float y, float z) {
        return new Keyframe(timestamp, KeyframeAnimations.posVec(x,y z), AnimationChannel.Interpolations.LINEAR)
    }
}