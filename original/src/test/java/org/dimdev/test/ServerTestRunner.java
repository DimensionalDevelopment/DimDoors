//package org.dimdev.test;
//
//import net.devtech.potatounit.TestRunner;
//import net.fabricmc.api.EnvType;
//import org.junit.runners.model.InitializationError;
//
//public class ServerTestRunner extends TestRunner {
//	public ServerTestRunner(Class<?> testClass) throws InitializationError, ReflectiveOperationException {
//		super(hackyOrderMethod(testClass), EnvType.SERVER);
//	}
//
//	private static Class<?> hackyOrderMethod(Class<?> testClass) {
//		System.setProperty("fabric.dli.main", "net.fabricmc.loader.launch.knot.KnotServer");
//		return testClass;
//	}
//}
