package fr.sparna.rdf.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.nio.file.ClosedWatchServiceException;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.io.File;



public class watchDir {

    private final Logger log = LoggerFactory.getLogger(watchDir.class.getName());

    private WatchService watchService;
    private File fileIn;
    private Map<WatchKey, Path> watchKeyToPath = new HashMap<>();

    public watchDir(List<File> fileInput) throws IOException  {        
        // Start Watch Service
        this.watchService = FileSystems.getDefault().newWatchService();
        this.fileIn = fileInput.get(0);
        // Create  parameters
        if (fileInput.size() == 1) {
            this.registerWatchKey(fileInput.get(0).getAbsoluteFile().getParentFile());
        } else if (fileInput.size() > 1) {
            for (File infile : fileInput) {
                this.registerWatchKey(infile.getAbsoluteFile().getParentFile());
            }
        }
    }
    
    private boolean registerWatchKey(File dir) throws IOException {
        System.out.println("Directory: " + dir);
        if(!dir.isDirectory()) { return false; }
        Path filePath = Paths.get(dir.getAbsolutePath());
        // Register the directory with the watch service and add new event for ENTRY_MODIFY events
        WatchKey key = filePath.register(this.watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        this.watchKeyToPath.put(key, filePath);
        // ***********************************************
        this.watchKeyToPath.forEach((k, value) -> System.out.println("Director Registered : " + k + " " + value));
        // ***********************************************
        log.info("{} has been registered with watch service.", dir);
        return true;
    }

    private boolean isCorrectFile(Path modifiedFile){
        return modifiedFile.toString().contains(this.fileIn.getName());
    }
    
    public void run_validate() throws IOException, InterruptedException, ClosedWatchServiceException {
        
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            
            Path pathFile = Paths.get(this.fileIn.getAbsoluteFile().getParentFile().getAbsolutePath());
            pathFile.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);        

            System.out.println("Path registered : " + pathFile);
            System.out.println("Watch Service : " + watchService.toString());

            while (true) {
                // gets a watch key
                final WatchKey key = watchService.take();

                // retrieves pending events for a key.
                for (WatchEvent<?> event : key.pollEvents()) {

                    // retrieves the event type and count.
                    // gets the kind of event (create, delete) 
                    final Kind<?> kind = event.kind();

                    // handles OVERFLOW event
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    } 

                    final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) event;
                    final Path entry = watchEventPath.context();

                    

                    // outputs an event type and entry
                    watchService.close();
                }

                boolean valid = key.reset();

                //exit loop if the key is not valid
                if (!valid) {
                    System.out.println("Key is invalid!");
                    break;
                }
            }
            watchService.close();
        }    
        
    }

    
    public void run() {

        // wait for key to be signalled
        WatchKey key;

        while (true) {
            try { 
                System.out.println("Execute process of analize......");
                // gets a watch key
                if ((key = this.watchService.take()) == null) { break; }

                for (WatchEvent<?> event: key.pollEvents()) {
                    System.out.print("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");

                    WatchEvent.Kind<?> kind = event.kind();

                    // TBD - provide example of how OVERFLOW event is handled
                    if (kind == OVERFLOW) {
                        continue;
                    }

                    Path keyPath = this.watchKeyToPath.get(key);

                    // Context for directory entry event is the file name of entry
                    WatchEvent<Path> eventData = (WatchEvent<Path>) event;
                    Path eventContext = eventData.context();
                    //On reconstruit le chemin fichier qui a levé l'event avec le chemin du watchkey enregistré
                    Path modifiedFile = keyPath.resolve(eventContext);

                    if(kind == StandardWatchEventKinds.ENTRY_MODIFY && event.count() == 1){
                        System.out.println("Conversion made for " + this.fileIn + ".");                
                    }
                }
            } catch (InterruptedException e) {
                log.error("InterruptedException when try watchEvent()" + e);
            }
        }
    }
}