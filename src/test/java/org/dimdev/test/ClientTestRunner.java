//package org.dimdev.test;
//
//import net.devtech.potatounit.TestRunner;
//import net.fabricmc.api.EnvType;
//import org.junit.runners.model.InitializationError;
//
//public class ClientTestRunner extends TestRunner {
//	public ClientTestRunner(Class<?> testClass) throws InitializationError, ReflectiveOperationException {
//		super(hackyOrderMethod(testClass), EnvType.CLIENT);
//	}
//
//	private static Class<?> hackyOrderMethod(Class<?> testClass) {
//		System.setProperty("fabric.dli.main", "net.fabricmc.loader.launch.knot.KnotClient");
//		return testClass;
//	}
//}
