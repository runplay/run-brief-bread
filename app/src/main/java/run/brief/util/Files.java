package run.brief.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import run.brief.util.log.BLog;

public class Files {

    public static final String SDCARD_PATH= Environment.getExternalStorageDirectory().toString();
	public static final String HOME_PATH_FILES= SDCARD_PATH+"/Bread";
	public static final String HOME_PATH_ZIP_FILES=HOME_PATH_FILES+File.separator+"archived";
	public static final String FILENAME_RSS_FILTERS="FTR.dat";
	public static final String FILENAME_RSS_FEEDS="RSF.dat";
	public static final String FILENAME_RSS_CATEGORY="RSC.dat";

    public static String HOME_PATH_APP=null;//Environment.getExternalStorageDirectory().toString()+"/Android/data/run.brief";

    public static void setAppHomePath(Context context) {
        if(HOME_PATH_APP==null && context.getApplicationInfo()!=null) {
            HOME_PATH_APP = context.getApplicationInfo().dataDir + "/briefbread";
        }
		//Log.e("PATH",HOME_PATH_APP);
    }

    public static final String FILENAME_GENERAL_SETTINGS="BSE.dat";

	public static final String FILENAME_SEARCH_HISTORY="SHST.dat";
	public static final String FOLDER_TWITTER_IMAGES="_img_tw";

	public static final String FOLDER_IMAGES="_img";
	public static final String FOLDER_SOUND="_sound";
	public static final String FOLDER_VIDEO="_video";

	public static final String FILE_EXT_JPG="jpg";
	public static final String FILE_EXT_GIF="gif";
	public static final String FILE_EXT_BITMAP="bmp";
	public static final String FILE_EXT_VIDEO_MP4="mp4";
	public static final String FILE_EXT_SOUND_WAV="wav";


	public static int countFilesInPath(String folder) {
		File f=new File(folder);
		if(f.isDirectory())
			return f.listFiles().length-2;
		return 0;
	}

	public static File[] getFiles(String folder) {
		File fo = new File(folder);
		if(fo.isDirectory())
			return fo.listFiles();
		else
			return null;
	}

	public static String getSDCardFilePath() {
		return Environment.getExternalStorageDirectory().toString();
	}

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }

    public static String removeBriefFileExtension(String filename) {
        return filename.replaceAll(".brf","");
    }


	public static String getExtension(String fileOrFilePath) {
		if(fileOrFilePath!=null && fileOrFilePath.length()>2) {
			int extp=fileOrFilePath.lastIndexOf(".");
			if(extp>0) {
				return fileOrFilePath.substring(extp);
			}
		}
		return null;
	}
	public static String getFilenameLessExtension(String fileOrFilePath) {
		if(fileOrFilePath!=null && fileOrFilePath.length()>2) {
			int extp=fileOrFilePath.lastIndexOf(".");
			if(extp>0) {
				return fileOrFilePath.substring(0,extp);
			}
		}
		return null;
	}
	public static boolean isVideo(String filename) {
		filename=filename.toLowerCase();
		if(filename.endsWith(".mp4")
				|| filename.endsWith(".mov")
				) {
			return true;
		}
		return false;

	}
	public static boolean isImage(String filename) {
		filename=filename.toLowerCase();
		if(filename.endsWith(".jpg")
				|| filename.endsWith(".gif")
				|| filename.endsWith(".png")
				|| filename.endsWith(".jpeg")
				|| filename.endsWith(".tif")
				|| filename.endsWith(".tiff")
				|| filename.endsWith(".bmp")
				) {
			return true;
		}
		return false;			
		
	}



	public static String readTrimRawTextFile(Context ctx, int resId) {
		InputStream inputStream = ctx.getResources().openRawResource(resId);

		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;
		StringBuilder text = new StringBuilder();
		try {
			while ((line = buffreader.readLine()) != null) {
				text.append(line.trim());
			}
		}
		catch (IOException e) {
			return null;
		}
		return text.toString();
	}

	public static FileCreateTask newTwitterImageFile() {
		StringBuilder dir = new StringBuilder(HOME_PATH_FILES);
		dir.append(File.separator);
		dir.append(FOLDER_TWITTER_IMAGES);

		FileCreateTask f = new FileCreateTask(dir.toString(),newFileName(FILE_EXT_JPG));

		return f;
	}
	
	public static FileCreateTask newImageFileJpg() {    
		return newImageFileJpg(newFileName(FILE_EXT_JPG));
	}
	public static FileCreateTask newImageFileJpg(String filename) {
		StringBuilder dir = new StringBuilder(HOME_PATH_FILES);
		dir.append(File.separator);
		dir.append(FOLDER_IMAGES);

		FileCreateTask f = new FileCreateTask(dir.toString(),filename);

		return f;
	}
	public static FileCreateTask newImageFileBmp() {
		StringBuilder dir = new StringBuilder(HOME_PATH_FILES);
		dir.append(File.separator);
		dir.append(FOLDER_IMAGES);

		FileCreateTask f = new FileCreateTask(dir.toString(),newFileName(FILE_EXT_BITMAP));

		return f;
	}
	public static FileCreateTask newVideoFile() {
		StringBuilder dir = new StringBuilder(HOME_PATH_FILES);
		dir.append(File.separator);
		dir.append(FOLDER_VIDEO);

		FileCreateTask f = new FileCreateTask(dir.toString(),newFileName(FILE_EXT_VIDEO_MP4));

		return f;
	}
	public static FileCreateTask newSoundFile() {
		StringBuilder dir = new StringBuilder(HOME_PATH_FILES);
		dir.append(File.separator);
		dir.append(FOLDER_SOUND);

		FileCreateTask f = new FileCreateTask(dir.toString(),newFileName(FILE_EXT_SOUND_WAV));

		return f;
	}
	public static String newFileName(String FILE_EXT_) {
		Date d= new Date();
		return d.getTime()+ File.separator+FILE_EXT_;
	}
	public static String getFileNameFromPath(String fullpath) {
		
		if(fullpath.indexOf("/")!=-1) {
			String str[] = fullpath.split("/");
			if(str!=null) {
				return str[str.length-1];
			}
		} else {
			return fullpath;
		}
		
		return "";
	}
	public static String getPathLessFileName(String fullpath) {
		
		if(fullpath.indexOf("/")!=-1) {
			
			return fullpath.substring(0,fullpath.lastIndexOf("/")+1);
			
		} else {
			return fullpath;
		}

	}
	public static String createFileNameFromUrl(final String url) {
		String turl=url.replaceFirst("http://", "");
		if(turl.contains("?")) {
			turl=turl.substring(0,turl.indexOf("?"));
		}
		//BLog.e(turl);
		int start=turl.lastIndexOf(".");
		String ext = turl.substring(start);

		//url=url.replaceAll("/","-");
		
		turl=turl.replaceAll("[^_A-Za-z0-9]", "");
		//BLog.e(turl+" -- "+ext);
		return turl+ext;
	}
    public static String createFilePathFromUrl(String url) {
        StringBuilder dir = new StringBuilder(Files.HOME_PATH_FILES);
        dir.append(File.separator);
        dir.append(Files.FOLDER_IMAGES);
        String path=dir.toString();
        String filename = Files.createFileNameFromUrl(url);


        dir.append(File.separator);
        dir.append(filename);
        return dir.toString();
    }
	public static boolean ensurePath(String path) {
		boolean ok=true;
		File filedir = new File(path);
		if(!filedir.exists())
			ok=filedir.mkdirs();

		return ok;
	}
	public static boolean ensurePathAndFile(String path, String filename) {
		boolean ok=true;
		File filedir = new File(path);
		if(!filedir.exists())
			ok=filedir.mkdirs();
		if(ok) {
			File file = new File(path+ File.separator+filename);
			try {
			if(!file.exists())
				file.createNewFile();
			} catch(IOException e) {
				BLog.add("EnsurePathAndFile", e);
			}
			
			ok=(file.exists() && file.canWrite());

		}
		return ok;
	}
	public static String getAvailableIncrementedFilePath(String requestedFileNameAndPath) {
		File f = new File(requestedFileNameAndPath);
		String path=f.getParentFile().getAbsolutePath();
		Files.ensurePath(path);
		if(!f.exists())
			return requestedFileNameAndPath;
		String newfilename=null;
		String []splits = f.getName().split("\\.");
		String fnle = splits[0];
		String fnler="";
		if(splits.length>1) {
			for(int i=1; i<splits.length;i++)
				fnler+="."+splits[i];
		}
        if(fnle.indexOf("-")!=-1) {
            String subfn = fnle.substring(0,fnle.lastIndexOf("-"));
            String co = fnle.substring(fnle.lastIndexOf("-")+1,fnle.length());
            //BLog.e("splitting name: "+co);
            if(Sf.toInt(co)>0)
                fnle=subfn;
        }
		//String ext = Files.getExtension(f.getName());
		for(int i=1; i<1000; i++) {
			f=new File(path+File.separator+fnle+"-"+i+fnler);
			if(!f.exists()) {
				break;
			}
		}
		//Log.e("FN","available name: "+f.getAbsolutePath());
		return f.getAbsolutePath();
	}

}
