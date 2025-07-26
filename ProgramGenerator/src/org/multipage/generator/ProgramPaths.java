/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-02-18
 *
 */
package org.multipage.generator;

import java.util.function.Supplier;

import org.maclan.MiddleUtility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Helper class for application paths.
 * @author vakol
 *
 */
public class ProgramPaths {
	
	/**
	 * Program path.
	 * @author user
	 *
	 */
	public static class PathSupplier {
		
		/**
		 * Caption.
		 */
		public String caption;
				
		/**
		 * Replacement tag.
		 */
		public String tag;
		
		/**
		 * Path supplier.
		 */
		public Supplier<String> supplier;

		/**
		 * Constructor.
		 */
		public PathSupplier(String captionResource, String tag, Supplier<String> supplier) {
			try {
				
				this.caption = Resources.getString(captionResource);
				this.tag = tag;
				this.supplier = supplier;
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}
		
		/**
		 * Default constructor.
		 */
		public PathSupplier() {
			
		}

		/**
		 * Create new path supplier.
		 * @param captionText
		 * @param solvedPath
		 * @return
		 */
		public static PathSupplier newAreaPath(String captionText, String tag, String solvedPath) {
			
			try {
				PathSupplier pathSupplier = new PathSupplier();
				
				pathSupplier.caption = captionText;
				pathSupplier.tag = tag;
				pathSupplier.supplier = () -> {
					return solvedPath;
				};
				
				return pathSupplier;
			}
			catch (Throwable e) {
				Safe.exception(e);
			}
			return null;
		}
	}
	
	/**
	 * Application path suppliers.
	 */
	public static PathSupplier userDirectorySupplier = new PathSupplier("org.multipage.generator.textUserDirectorySupplier", "[@PATH $user_dir]", () -> { return MiddleUtility.getUserDirectory(); });
	public static PathSupplier databaseDirectorySupplier = new PathSupplier("org.multipage.generator.textDatabaseDirectorySupplier", "[@PATH $database_dir]", () -> { return MiddleUtility.getDatabaseAccess(); });
	public static PathSupplier webInterfaceDirectorySupplier = new PathSupplier("org.multipage.generator.textWebInterfaceDirectorySupplier", "[@PATH $web_dir]", () -> { try { return MiddleUtility.getWebInterfaceDirectory(); } catch (Exception e) { return null; } });
	public static PathSupplier phpDirectorySupplier = new PathSupplier("org.multipage.generator.textPhpDirectorySupplier", "[@PATH $php_dir]", () -> { return MiddleUtility.getPhpDirectory(); });
	public static PathSupplier temporaryDirectory = new PathSupplier("org.multipage.generator.textTemporaryDirectory", "[@PATH $temp_dir]", () -> { return MiddleUtility.getTemporaryDirectory(); });
}
