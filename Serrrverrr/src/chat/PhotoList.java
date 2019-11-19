/**
 * 
 */
package chat;

import java.io.File;

public class PhotoList {

	public String makeImgList() {
		StringBuffer sb = new StringBuffer();

		File dir = new File("/photo");
		File[] fileList = dir.listFiles();

		for (File img : fileList) {
			sb.append(img.getName() + ":");
		}

		return sb.toString();

	}
}
