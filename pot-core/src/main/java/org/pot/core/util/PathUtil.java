package org.pot.core.util;

import java.util.LinkedList;
import org.apache.commons.lang3.StringUtils;

/**
 * 路径相关的工具
 * @author sunchong
 */
public class PathUtil {
	/**
	 * 获取操作系统名字
	 */
	public static final String SYS_OS = System.getProperty("os.name");
	/**
	 * 程序所在路径
	 */
	public static final String SYS_PATH = System.getProperty("user.dir");
	/**
	 * 判断路径是不是相对路径
	 * @param path
	 * @return
	 */
	private static boolean isRelativePath(String path){
		if(path.startsWith("./") || path.startsWith("../")){
			return true;
		}
		return false;
	}
	
	/**
	 * 通过相对路径获取绝对路径
	 * @param relativePath 相对路径
	 * @return
	 */
	public static String getAbsolutePath(String relativePath){
		if(!isRelativePath(relativePath)){
			if(!relativePath.endsWith("/") && !relativePath.endsWith("\\")){
				relativePath += "/";
			}
			return relativePath;
		}
		
		boolean isWindows = SYS_OS.toLowerCase().startsWith("windows");
		String[] ss = SYS_PATH.replace("\\", "/").split("/");
		LinkedList<String> list = new LinkedList<String>();
		for(String s : ss){
			list.add(s);
		}
		String[] ss1 = relativePath.replace("\\", "/").split("/");
		for(String s : ss1){
			if(StringUtils.equals(".", s)){
				continue;
			}
			if(StringUtils.equals("..", s)){
				//如果是windows系统，只剩下盘符不能在获取上级目录了
				if(isWindows && list.size() == 1){
					continue;
				}
				list.pollLast();
			}else{
				list.add(s);
			}
		}
		StringBuilder sb = new StringBuilder();
		if(!isWindows){
			sb.append("/");
		}
		for(String s : list){
			sb.append(s).append("/");
		}
		return sb.toString();
	}
}
