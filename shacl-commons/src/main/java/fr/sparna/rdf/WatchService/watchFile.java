package fr.sparna.rdf.WatchService;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class watchFile {

    // Log Shacl-Play
    private final Logger log = LoggerFactory.getLogger(watchFile.class.getName());

    
    private Path targetFile;
    private File inputFile;
    private List<File> fileIn;
    private Runnable taskShaclPlay;
    private Path pathFile;

    public watchFile(File input,Runnable taskShaclPlay) {
        // One File
        this.inputFile = input;
        this.taskShaclPlay = taskShaclPlay;
        // Get path absolute
        this.pathFile = Paths.get(input.getAbsoluteFile().getParentFile().getAbsolutePath());
    }

    public void runWatchFile() throws IOException {
    
        // We obtain the file system of the Path
        FileSystem fileSystem = this.pathFile.getFileSystem();
        // We create the new WatchService using the try-with-resources block
        try (WatchService watchService = fileSystem.newWatchService()) {
            // We watch for modification events
            this.pathFile.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
             // Start the infinite polling loop
            while (true) {
                // Wait for the next event
                WatchKey key = watchService.take();
                for (WatchEvent event : key.pollEvents()) {
                    // Get the type of the event
                    Kind<?> kind = event.kind();
                    //
                    if (kind == StandardWatchEventKinds.OVERFLOW) { continue; }

                    final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) event;
                    final Path changedFile = watchEventPath.context();

                    // && event.count() == 1
                    if(kind == StandardWatchEventKinds.ENTRY_MODIFY && event.count() == 1){
                        // Call mehod to process the SHACL file
                        System.out.println("The file: "+ this.inputFile + " is modified. The output file document is updated. " );
                        this.taskShaclPlay.run();
                    }
                }
                boolean valid = key.reset();
                //exit loop if the key is not valid
                if (!valid) {
                    System.out.println("Key is invalid!");
                    break;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            this.taskShaclPlay.run();
            // Send message the processus succesfull
            System.out.println("The document is generated.");
        }
    }
}