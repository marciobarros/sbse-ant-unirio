package unirio.ant.architecture;

import javax.management.modelmbean.XMLParseException;

import unirio.ant.controller.CDAReader;
import unirio.ant.model.Project;
import unirio.ant.model.ProjectClass;
import unirio.ant.model.ProjectPackage;

/**
 * Class that loads real and optimized versions of Apache Ant
 * 
 * @author Marcio
 */
public class ProjectLoader
{
	/**
	 * Input directory for real versions
	 */
	private static String INPUT_DIRECTORY = "data\\odem\\";

	/**
	 * Names of the real versions
	 */
	private static String[] REAL_VERSIONS = 
	{
		"apache-ant-1.1.0",
		"apache-ant-1.2.0",
		"apache-ant-1.3.0",
		"apache-ant-1.4.0",
		"apache-ant-1.4.1",
		"apache-ant-1.5.0",
		"apache-ant-1.5.1",
		"apache-ant-1.5.2",
		"apache-ant-1.5.3",
		"apache-ant-1.5.4",
		"apache-ant-1.6.0",
		"apache-ant-1.6.1",
		"apache-ant-1.6.2",
		"apache-ant-1.6.3",
		"apache-ant-1.6.4",
		"apache-ant-1.6.5",
		"apache-ant-1.7.0",
		"apache-ant-1.7.1",
		"apache-ant-1.8.0",
		"apache-ant-1.8.1",
		"apache-ant-1.8.2",
		"apache-ant-1.8.3",
		"apache-ant-1.8.4",
		"apache-ant-1.9.0"
	};
	
	/**
	 * Known external dependencies for real versions
	 */
	private static String[] REAL_VERSION_EXTERNAL_DEPENDENCIES = 
	{
		// Versão 1.1.0
		"org.apache.tools.ant.taskdefs.optional.XalanLiaison",
		"org.apache.tools.ant.taskdefs.optional.XslpLiaison",
		// Versão 1.3.0
		"org.apache.tools.ant.taskdefs.optional.TraXLiaison",
		// Versão 1.4.0
		"at.dms.kjc.Main",
		// Versão 1.5.0
		"org.apache.tools.ant.taskdefs.optional.Test",
		"org.apache.tools.ant.taskdefs.Get",
		"org.apache.tools.ant.taskdefs.email.UUMailer",
		"org.apache.tools.ant.taskdefs.email.MimeMailer",
		// Versão 1.6.0
		"org.apache.tools.ant.launch.AntMain",
		"org.apache.tools.ant.launch.Locator",
		"org.apache.tools.ant.util.optional.WeakishReference12",
		// Versão 1.7.0
		"org.apache.tools.ant.launch.Launcher",
		"org.apache.tools.ant.util.java15.ProxyDiagnostics",
		"org.apache.tools.ant.filters.util.JavaClassHelper",
		// Versão 1.8.0
		"org.apache.tools.ant.loader.AntClassLoader5",
		"org.apache.tools.ant.taskdefs.optional.EchoProperties"
	};

	/**
	 * Optimized version for EVM
	 */
	private static String OPTIMIZED_EVM = "206 83 60 88 16 61 61 60 60 88 194 63 63 63 195 52 22 231 66 231 66 231 66 67 200 203 228 168 71 72 72 209 71 212 218 220 213 240 242 245 243 81 72 246 71 247 71 71 16 249 61 88 254 260 136 16 29 269 273 275 95 95 96 96 153 8 8 276 101 101 101 101 70 102 70 70 103 278 70 103 70 40 266 0 0 0 1 1 1 106 107 107 106 107 107 106 279 109 109 282 109 284 283 306 109 109 107 110 110 262 335 118 118 119 119 340 342 343 123 123 345 348 350 127 2 2 129 127 359 129 131 132 133 134 2 2 2 224 224 135 67 67 135 70 135 135 135 95 135 4 137 138 70 4 139 4 70 4 67 5 140 5 5 141 5 5 142 102 143 143 143 88 88 144 152 171 145 101 6 6 8 145 8 145 98 98 99 146 116 116 116 148 148 148 148 149 149 150 150 103 151 151 94 73 155 95 156 157 116 147 158 159 147 37 176 161 162 163 164 163 163 163 165 163 166 167 167 169 339 170 327 59 193 173 174 175 173 173 7 178 7 178 177 179 179 179 179 52 180 181 182 182 183 183 184 184 99 185 186 185 185 185 185 187 188 189 186 190 16 191 192 37 191 24 62 62 62 196 197 198 199 64 64 64 201 202 68 68 68 204 69 204 69 69 69 205 96 59 5 141 83 207 208 232 65 191 256 10 210 211 74 74 74 75 75 239 214 75 215 216 217 74 76 76 219 77 77 221 222 221 223 77 78 224 78 225 239 3 227 226 105 229 230 105 154 70 234 65 65 65 233 65 172 65 22 235 121 236 73 237 238 79 79 3 241 80 80 81 81 82 82 244 83 84 84 84 85 85 248 86 86 61 250 191 251 252 253 87 87 87 255 257 51 38 258 259 89 89 261 263 99 264 85 116 265 90 90 267 268 91 91 270 271 272 313 92 92 92 321 274 93 93 93 271 94 97 97 277 100 100 100 100 104 104 281 280 108 108 108 111 111 113 112 112 285 286 113 14 108 287 288 289 290 291 292 293 294 296 295 9 9 9 150 150 297 298 10 10 299 300 302 301 10 10 10 10 303 304 114 114 305 308 310 309 307 311 312 315 316 92 314 92 318 114 114 317 114 319 320 160 37 322 323 324 329 325 60 326 11 12 11 11 98 328 332 330 11 12 12 13 331 13 333 13 13 13 334 22 115 336 337 115 115 338 25 117 117 29 341 120 120 120 120 121 344 3 122 122 346 111 124 347 124 124 14 125 349 125 125 15 15 15 351 352 353 354 355 358 356 16 16 16 16 16 16 16 50 17 17 357 126 126 126 17 17 18 128 18 18 360 18 361 128 18 128 18 18 362 18 130 130 6 19 19 19 19 19 19 19 19 19 19 19 19 19 19 19 19 19 19 19 19 19 19 19 19 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 21 21 21 22 23 23 23 23 23 23 24 24 24 25 25 25 25 25 26 26 26 26 27 27 27 27 27 27 28 28 28 28 29 29 29 29 29 30 30 30 30 31 31 32 32 32 32 33 33 33 33 33 33 34 34 34 34 34 35 35 35 35 36 36 36 36 36 36 36 36 36 36 36 36 36 37 38 38 38 38 38 38 38 38 38 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 40 40 41 41 41 41 41 42 42 42 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 44 44 44 44 44 44 44 44 44 44 45 45 45 45 45 45 45 45 45 45 45 45 45 45 45 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 47 47 47 47 47 47 47 47 47 47 47 48 48 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 50 50 51 51 52 53 53 53 54 54 54 54 54 54 54 55 55 55 55 55 55 56 56 56 56 57 57 57 57 57 57 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58";

	/**
	 * Optimized version for MQ
	 */
	private static String OPTIMIZED_MQ = "89 89 82 6 102 89 89 82 82 6 59 21 21 21 52 52 77 32 0 32 0 32 0 65 53 59 68 35 37 0 0 0 37 0 0 0 0 0 0 0 0 73 0 37 37 0 37 0 98 0 59 6 96 100 82 100 99 61 61 61 61 61 61 61 8 8 8 8 8 8 8 8 68 89 68 98 35 98 82 35 82 40 82 62 62 62 1 35 1 63 2 2 63 2 2 63 74 74 74 74 74 74 74 74 74 74 2 2 2 95 2 2 2 2 2 49 63 2 2 2 2 2 2 63 7 7 7 63 7 7 7 7 7 7 7 7 7 3 3 64 65 65 64 64 64 64 64 64 64 4 68 4 68 4 4 4 68 4 65 5 5 72 5 5 72 5 6 6 6 6 6 6 6 6 89 89 59 8 8 8 8 59 8 59 86 86 95 71 71 71 71 69 69 69 69 82 83 10 10 35 70 70 85 85 98 61 100 102 71 78 78 78 78 96 89 43 60 93 93 93 93 93 93 93 11 22 22 98 77 98 86 83 77 60 60 60 60 60 46 100 46 46 98 39 39 39 39 52 98 102 43 43 99 99 75 75 95 16 16 16 16 16 16 16 16 16 16 16 97 39 39 39 24 24 11 11 11 39 98 75 75 75 75 75 85 85 84 84 84 76 76 76 76 76 76 100 43 72 72 72 98 101 101 101 101 39 51 10 81 81 80 80 99 81 81 81 81 81 81 81 81 81 24 24 102 9 9 9 9 9 9 9 3 98 3 100 99 24 67 67 67 67 67 67 67 67 77 9 9 9 100 9 9 9 46 98 77 100 85 79 79 79 79 24 88 88 88 73 73 9 9 61 98 87 87 87 24 24 66 66 66 89 49 49 98 9 9 9 9 9 100 98 51 38 90 90 90 90 13 82 95 98 24 39 22 22 22 91 91 91 91 98 60 100 92 92 92 92 39 102 9 9 9 60 85 85 85 49 94 94 94 94 11 11 102 89 9 9 9 9 9 9 9 9 9 9 9 99 9 102 45 49 49 49 49 45 45 101 49 49 101 49 10 10 10 10 10 10 10 98 10 96 10 10 10 10 11 100 11 11 11 11 11 49 99 11 97 11 100 92 92 92 99 99 11 95 11 54 11 11 96 99 100 99 102 11 82 11 12 12 12 12 86 86 12 86 12 12 12 13 13 13 13 13 13 13 13 77 14 14 14 14 14 25 25 29 29 29 14 14 14 14 14 77 98 24 14 14 100 9 14 14 14 14 99 15 15 15 15 102 15 15 97 97 97 96 97 97 97 97 97 97 97 97 97 97 50 17 17 17 17 17 17 17 17 18 18 18 18 18 18 18 18 18 18 18 18 18 18 19 19 95 19 19 19 19 19 19 19 19 19 19 19 19 19 19 19 98 19 19 19 19 19 19 19 19 20 20 39 20 20 20 20 20 20 20 20 20 20 20 20 20 21 21 21 22 23 23 23 23 23 23 24 24 24 25 25 25 25 25 26 26 26 26 27 27 27 27 27 27 28 28 28 28 29 29 29 29 29 30 30 30 30 31 31 32 32 32 32 33 33 33 33 33 33 34 34 34 34 34 35 35 35 35 36 36 36 36 36 36 36 36 36 36 36 36 36 37 38 38 38 38 38 38 38 38 38 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 40 40 41 41 41 41 41 42 42 42 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 43 44 44 44 44 44 44 44 44 44 44 45 45 45 45 45 45 45 45 45 45 45 45 45 45 45 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 47 47 47 47 47 47 47 47 47 47 47 48 48 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 49 50 50 51 51 52 53 53 53 54 54 54 54 54 54 54 55 55 55 55 55 55 56 56 56 56 57 57 57 57 57 57 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58 58";
	
	/**
	 * Returns all real versions
	 */
	public String[] getRealVersions()
	{
		return REAL_VERSIONS;
	}
	
	/**
	 * Loads a real version into a project
	 */
	public Project loadRealVersion(String versao) throws XMLParseException
	{
		CDAReader reader = new CDAReader();
		reader.setIgnoredClasses(REAL_VERSION_EXTERNAL_DEPENDENCIES);
		return reader.execute(INPUT_DIRECTORY + versao + ".odem");
	}
	
	/**
	 * Loads an optimized version into a project
	 */
	private Project loadOptimizedVersion(String solution) throws XMLParseException
	{
		Project project = new CDAReader().execute("data\\odem\\apache_ant.odem");
		project.clearPackages();

		String[] tokens = solution.split(" ");
		
		for (int i = 0; i < tokens.length; i++)
		{
			ProjectClass _class = project.getClassIndex(i);
			int packageNumber = Integer.parseInt(tokens[i]);
			
			while (project.getPackageCount() <= packageNumber)
				project.addPackage("" + project.getPackageCount());
			
			ProjectPackage _package = project.getPackageIndex(packageNumber);
			_class.setPackage(_package);
		}
		
		return project;
	}
	
	/**
	 * Loads the version optimized for EVM
	 */
	public Project loadOptimizedVersionEVM() throws XMLParseException
	{
		return loadOptimizedVersion(OPTIMIZED_EVM);
	}

	/**
	 * Loads the version optimized for MQ
	 */
	public Project loadOptimizedVersionMQ() throws XMLParseException
	{
		return loadOptimizedVersion(OPTIMIZED_MQ);
	}
}