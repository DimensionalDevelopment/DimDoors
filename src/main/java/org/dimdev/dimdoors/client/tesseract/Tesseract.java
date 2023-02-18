package org.dimdev.dimdoors.client.tesseract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;

import com.flowpowered.math.vector.Vector4f;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Direction;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.dimdev.dimdoors.api.util.RGBA;

@Environment(EnvType.CLIENT)
public class Tesseract {

	private static final Logger LOGGER = LoggerFactory.getLogger("tesseract");

	// first get the cell, and then get the face of that cell you want to render.
	// As long as the inner Direction3s are looped in the same order every time the resulting 4cube will have cell symmetry
	public static final EnumMap<Q8, EnumMap<Q8,Plane>> CELL_DIRECTION_TO_PLANES = new EnumMap<>(Q8.class);

	static {
		//for every cell
		for(Q8 cell : Q8.values()){
			//the planes for this cell
			EnumMap<Q8,Plane> cell_planes = new EnumMap<>(Q8.class);
			Iterator<Q8> directionsLoop = Iterables.cycle(Q8.POSITIVE_IMAG).iterator();
			//for every +ve direction wrt the tesseract axis, do all if we need to render both sides but backface culling should technically work
			for(Q8 internalDirection : Q8.POSITIVE_IMAG){
				Q8 externalDirection = cell.mul(directionsLoop.next()); // .next() gives the internal direction here
				Q8 axis_u            = cell.mul(directionsLoop.next()); //gives which axis the uvtexture's u will be in
				Q8 axis_v            = cell.mul(directionsLoop.next()); //gives which axis the uvtexture's v will be in
				LOGGER.info("Cell: {}, Dir(internal): {} --> out: {}, u: {}, v: {}",
						cell,
						internalDirection,
						externalDirection,
						axis_u,
						axis_v
				);

				{//+ve side
					Vector4f plane_center = cell.getVector().add(externalDirection.getVector());

					Vector4f p00 = plane_center.sub(axis_u.getVector()).sub(axis_v.getVector()).mul(0.5f);
					Vector4f p01 = plane_center.sub(axis_u.getVector()).add(axis_v.getVector()).mul(0.5f);
					Vector4f p11 = plane_center.add(axis_u.getVector()).add(axis_v.getVector()).mul(0.5f);
					Vector4f p10 = plane_center.add(axis_u.getVector()).sub(axis_v.getVector()).mul(0.5f);

					Plane plane = new Plane(p00, p01, p11, p10);
					cell_planes.put(internalDirection, plane);
				}

				directionsLoop.next(); // so that it matches with the next loop
			}
			CELL_DIRECTION_TO_PLANES.put(cell, cell_planes);
		}
	}

    @Environment(EnvType.CLIENT)
    public void draw(org.joml.Matrix4f model, VertexConsumer vc, RGBA color, double radian) {

		CELL_DIRECTION_TO_PLANES.forEach((cell, face_to_plane) -> {
			face_to_plane.forEach((face, plane) -> {
				assert (face.direction != null);
				//FIXME this is where you then use this to choose the side of the texturecube you want to get the direction from
				Direction texture_direction = face.direction;
				/*LOGGER.info("Rendering in direction {} plane {}",
						texture_direction,
						plane
						);*/
            	plane.draw(model, vc, color, radian);
			});
		});
    }

	private enum Q8{
		PI(0, "+i", Direction.EAST , +1, new Vector4f(+1,  0,  0,  0), new RGBA( 1   , 0   , 0   , 0.5f)),
		NI(1, "-i", Direction.WEST , -1, new Vector4f(-1,  0,  0,  0), new RGBA( 0   , 0.7f, 0.7f, 0.5f)),
		PJ(2, "+j", Direction.UP   , +1, new Vector4f( 0, +1,  0,  0), new RGBA( 0   , 1   , 0   , 0.5f)),
		NJ(3, "-j", Direction.DOWN , -1, new Vector4f( 0, -1,  0,  0), new RGBA( 0.7f, 0   , 0.7f, 0.5f)),
		PK(4, "+k", Direction.SOUTH, +1, new Vector4f( 0,  0, +1,  0), new RGBA( 0   , 0   , 1   , 0.5f)),
		NK(5, "-k", Direction.NORTH, -1, new Vector4f( 0,  0, -1,  0), new RGBA( 0.7f, 0.7f, 0   , 0.5f)),
		PU(6, "+1", null, +1, new Vector4f( 0,  0,  0, +1), new RGBA( 0.57f, 0.57f, 0.57f, 0.5f)),
		NU(7, "-1", null, -1, new Vector4f( 0,  0,  0, -1), new RGBA( 0, 0, 0, 0.5f));

		private static final Q8 I = Q8.PI;
		private static final Q8 J = Q8.PJ;
		private static final Q8 K = Q8.PK;
		private static final Q8 U = Q8.PU; // ==1

		private static final Q8[] REAL = Arrays.stream(Q8.values()).filter(q8 ->
				q8.direction == null
		).toArray(Q8[]::new);

		//act like vectors
		private static final Q8[] IMAGINARY = Arrays.stream(Q8.values()).filter(q8 ->
				q8.direction != null
		).toArray(Q8[]::new);

		private static final Q8[] POSITIVE = Arrays.stream(Q8.values()).filter(q8 ->
				q8.axisDirection > 0
		).toArray(Q8[]::new);

		private static final Q8[] POSITIVE_IMAG = Arrays.stream(IMAGINARY).filter(q8 ->
				q8.axisDirection > 0
		).toArray(Q8[]::new);
		private static final Q8[] NEGATIVE_IMAG = Arrays.stream(IMAGINARY).filter(q8 ->
				q8.axisDirection < 0
		).toArray(Q8[]::new);
		static {
			ArrayUtils.reverse(NEGATIVE_IMAG);//reverses order, jank
		}

		//left * right = MUL_MAT[right][left]
		private static final Q8[][] MUL_MAT = new Q8[][]
				{			        //right
			//left     //+i  -i  +j  -j  +k  -k  +1  -1
			/* +i */ 	{NU, PU, NK, PK, PJ, NJ, PI, NI},
			/* -i */	{PU, NU, PK, NK, NJ, PJ, NI, PI},
			/* +j */	{PK, NK, NU, PU, NI, PI, PJ, NJ},
			/* -j */	{NK, PK, PU, NU, PI, NI, NJ, PJ},
			/* +k */	{NJ, PJ, PI, NI, NU, PU, PK, NK},
			/* -k */	{PJ, NJ, NI, PI, PU, NU, NK, PK},
			/* +1 */	{PI, NI, PJ, NJ, PK, NK, PU, NU},
			/* -1 */	{NI, PI, NJ, PJ, NK, PK, NU, PU},
				};


		private final int id;
		private final String name;
		private final Direction direction;
		private final int axisDirection;
		private final Vector4f vector;

		private final RGBA color;

		Q8(int id, String name, Direction direction, int axisDirection, Vector4f vector, RGBA color) {
			this.id = id;
			this.name = name;
			this.direction = direction;
			this.axisDirection = axisDirection;
			this.vector = vector;
			this.color = color;
		}

		public static Q8 mul(Q8 left, Q8 right) {
			return MUL_MAT[right.id][left.id];
		}
		public Q8 mul(Q8 right){
			return Q8.mul(this, right);
		}

		public int getId(){
			return id;
		}

		public String getName(){
			return name;
		}

		public Direction getDirection() {
			return direction;
		}

		public int getAxisDirection() {
			return axisDirection;
		}

		public Vector4f getVector() {
			return vector;
		}

		public RGBA getColor(){
			return color;
		}

		public String toString(){
			return name;
		}

	}
}
