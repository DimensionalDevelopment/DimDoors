package org.dimdev.vanillafix.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.crash.CrashReport;
import net.minecraft.init.Bootstrap;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;
import org.dimdev.vanillafix.GuiCrashScreen;
import org.lwjgl.LWJGLException;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings({"unused", "NonConstantFieldWithUpperCaseName", "RedundantThrows"}) // Shadow
@SideOnly(Side.CLIENT)
@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IThreadListener, ISnooperInfo {

    @Shadow volatile boolean running;
    @Shadow private boolean hasCrashed;
    @Shadow private CrashReport crashReporter;

    @Shadow private void init() throws LWJGLException, IOException {}

    @Shadow private void runGameLoop() throws IOException {}

    @Shadow public void freeMemory() {}

    @Shadow public void displayGuiScreen(@Nullable GuiScreen guiScreenIn) {}

    @Shadow public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash) { return null; }

    @Shadow @Final private static Logger LOGGER;

    @Shadow public void shutdownMinecraftApplet() {}

    @Shadow public void displayCrashReport(CrashReport crashReportIn) {}

    @SuppressWarnings("CallToSystemGC")
    @Overwrite
    public void run() {
        running = true;

        try {
            init();
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Initializing game");
            crashreport.makeCategory("Initialization");
            displayCrashReport(addGraphicsAndWorldToCrashReport(crashreport)); // TODO: GUI for this too
            return;
        }

        try {
            while (running) {
                if (!hasCrashed || crashReporter == null) {
                    try {
                        runGameLoop();
                    } catch (OutOfMemoryError e) {
                        freeMemory();
                        displayGuiScreen(new GuiMemoryErrorScreen());
                        System.gc();
                    } catch (ReportedException e) {
                        addGraphicsAndWorldToCrashReport(e.getCrashReport());
                        freeMemory();
                        LOGGER.fatal("Reported exception thrown!", e);
                        displayCrashScreen(e.getCrashReport());
                    } catch (Throwable e) {
                        CrashReport report = addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", e));
                        freeMemory();
                        LOGGER.fatal("Unreported exception thrown!", e);
                        displayCrashScreen(report);
                    }
                } else {
                    displayCrashReport(crashReporter);
                }
            }
        } catch (MinecraftError ignored) {
        } finally {
            shutdownMinecraftApplet();
        }
    }

    public void displayCrashScreen(CrashReport report) {
        try {
            File crashReportsDir = new File(Minecraft.getMinecraft().mcDataDir, "crash-reports");
            File crashReportSaveFile = new File(crashReportsDir, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");

            // Print the report in bootstrap
            Bootstrap.printToSYSOUT(report.getCompleteReport());

            // Save the report and print file in bootstrap
            File reportFile = null;
            if (report.getFile() != null) {
                reportFile = report.getFile();
            } else if (report.saveToFile(crashReportSaveFile)) {
                reportFile = crashReportSaveFile;
            }

            if (reportFile != null) {
                Bootstrap.printToSYSOUT("Recoverable game crash! Crash report saved to: " + reportFile);
            } else {
                Bootstrap.printToSYSOUT("Recoverable game crash! Crash report could not be saved.");
            }

            // Display the crash screen
            displayGuiScreen(new GuiCrashScreen(reportFile, report));

            // Keep running
            hasCrashed = false;
        } catch (Throwable e) {
            LOGGER.error("The crash screen threw an error, reverting to default crash report", e);
            displayCrashReport(report);
        }
    }
}
