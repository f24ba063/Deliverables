package pack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageFileMover {
    public static void main(String[] args) {
        Path source = Paths.get("C://images");
        Path destination = Paths.get("C://movd_images");
        try {
        	if(!Files.exists(destination)) {
        		Files.createDirectories(destination);
        	}
        	
        	Map<Long, List<Path>> sizeToFileMap  = new HashMap<>();
        	
        	Files.walk(source)
        	.filter(path -> Files.isRegularFile(path) && 
        			(path.toString().endsWith(".jpg")||path.toString().endsWith(".png")))
        	.forEach(file -> {
        		try {
        			long size = Files.size(file);
        			sizeToFileMap.computeIfAbsent(size, k -> new ArrayList<>()).add(file);
        		}catch(IOException e) {
        			e.printStackTrace();
        		}
        	});
        	
        	
        	
        	for(List<Path> files: sizeToFileMap.values()) {
        		if(files.size() > 1) {
        			for(int i = 1; i < files.size(); i++) {
        				Path fileToMove = files.get(i);

        				if(Files.exists(fileToMove)) {
        					Path sendFor = destination.resolve(fileToMove.getFileName());
        					
        					int count = 1;
        					while(Files.exists(sendFor)) {
        						String newFileName = String.format("%s_%d%s",
        								removeExtension(fileToMove.getFileName().toString()), count,
        								getExtension(fileToMove.getFileName().toString()));
        						sendFor = destination.resolve(newFileName);
        						count++;
        					}
        					
        					Files.move(fileToMove, sendFor, StandardCopyOption.REPLACE_EXISTING);
        					System.out.println("Moved: " + fileToMove + " → " + sendFor);
        				}
        			}
        		}        		

        	}
        }catch(IOException e) {
        	e.printStackTrace();
        }
    }
    private static String removeExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    private static String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }
}