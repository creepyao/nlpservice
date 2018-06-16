package com.yao.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;
import java.util.Date;

public class FileUtil {
    private static Date getCreateTime2(File file){
        Path path= Paths.get(file.getPath());
        BasicFileAttributeView basicview= Files.getFileAttributeView(path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS );
        BasicFileAttributes attr;
        try {
            attr = basicview.readAttributes();
            Date createDate = new Date(attr.creationTime().toMillis());
            return createDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.set(1970, 0, 1, 0, 0, 0);
        return cal.getTime();
    }
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    public static void writeString(String filePath, String content){
        try {
            File file = new File(filePath);
            // 判断文件是否存在
            if (file.exists()) {
                file.delete();
            }
            //文件夹路径不存在 则创建路径
            String folderPath = filePath.substring(0,filePath.lastIndexOf("/")+1);
            File folder = new File(folderPath);
            if (!folder.exists() && !folder.isDirectory()) {
                folder.mkdirs();
            }
            FileWriter writer=new FileWriter(filePath);
            writer.write(content);
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
