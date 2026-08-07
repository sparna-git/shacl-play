package fr.sparna.rdf.WatchService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class watchFile {

    // Log Shacl-Play
    private final Logger log = LoggerFactory.getLogger(watchDir.class.getName());

    private Path pathDirectory;
    private Path pathFile;
    private File inputFile;
    private List<File> fileIn;

    public watchFile(File input) {
        
        //List<Path> paths = input.stream().map( f -> Paths.get(f.getAbsoluteFile().getParentFile().getAbsolutePath())).collect(Collectors.toList());
        // One File
        this.inputFile = input;
        // Get path absolute
        this.pathDirectory = Paths.get(input.getAbsoluteFile().getParentFile().getAbsolutePath());
        this.pathFile = Paths.get(input.getAbsoluteFile().getParentFile().getAbsolutePath());
        System.out.println("Path file Directory: " + this.pathDirectory);
        System.out.println("Path file input: " + this.pathFile);

        // RegularFile
        /* 
        for (Path path : paths) {
            if (!Files.isRegularFile(path)) {
                // Do not allow this to be a folder since we want to watch files
                throw new IllegalArgumentException(path + " is not a regular file");
            }
        }
        */

        // One file
        /*
        if (!Files.isRegularFile(this.pathFile)) {
            // Do not allow this to be a folder since we want to watch files
            throw new IllegalArgumentException(this.pathFile + " is not a regular file");
        }
        */

    }

    public void runWatchFile() throws IOException {
    
        // We obtain the file system of the Path
        FileSystem fileSystem = this.pathFile.getFileSystem();
        System.out.println("File System of path: " + fileSystem.toString());
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

                    if(kind == StandardWatchEventKinds.ENTRY_MODIFY && event.count() == 1){
                        System.out.println("Conversion made for " + this.inputFile + ".");
                        watchService.close();                        
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
        }
    }
}