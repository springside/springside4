package org.springside.modules.utils.io;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springside.modules.utils.base.Platforms;
import org.springside.modules.utils.number.RandomUtil;
import org.springside.modules.utils.text.Charsets;

public class FileUtilTest {

	@Test
	public void readWrite() throws IOException {
		File file = FileUtil.createTempFile("abc", ".tmp").toFile();
		try {
			String content = "haha\nhehe";
			FileUtil.write(content, file);

			String result = FileUtil.toString(file);
			assertThat(result).isEqualTo(content);
			List<String> lines = FileUtil.toLines(file);
			assertThat(lines).containsExactly("haha", "hehe");

			FileUtil.append("kaka", file);
			assertThat(new String(FileUtil.toByteArray(file), Charsets.UTF_8)).isEqualTo("haha\nhehekaka");
		} finally {
			FileUtil.deleteFile(file);
		}
	}

	@Test
	public void opFiles() throws IOException {
		File file = new File(FilePathUtil.contact(Platforms.TMP_DIR, "testFile" + RandomUtil.nextInt()));
		FileUtil.touch(file);
		assertThat(FileUtil.isFileExists(file)).isTrue();
		FileUtil.touch(file);

		String content = "haha\nhehe";
		FileUtil.write(content, file);
		assertThat(FileUtil.toString(file)).isEqualTo(content);

		File newFile = new File(FilePathUtil.contact(Platforms.TMP_DIR, "testFile" + RandomUtil.nextInt()));
		File newFile2 = new File(FilePathUtil.contact(Platforms.TMP_DIR, "testFile" + RandomUtil.nextInt()));

		FileUtil.copyFile(file, newFile);
		assertThat(FileUtil.isFileExists(newFile)).isTrue();
		assertThat(FileUtil.toString(newFile)).isEqualTo(content);

		FileUtil.moveFile(newFile, newFile2);
		assertThat(FileUtil.toString(newFile2)).isEqualTo("haha\nhehe");


	}

	@Test
	public void opDir() throws IOException {
		String fileName = "testFile" + RandomUtil.nextInt();
		File dir = new File(FilePathUtil.contact(Platforms.TMP_DIR, "testDir"));

		File file = new File(FilePathUtil.contact(Platforms.TMP_DIR, "testDir", fileName));
		String content = "haha\nhehe";
		FileUtil.makesureDirExists(dir);
		FileUtil.write(content, file);

		File dir2 = new File(FilePathUtil.contact(Platforms.TMP_DIR, "testDir2"));
		FileUtil.copyDir(dir, dir2);
		File file2 = new File(FilePathUtil.contact(Platforms.TMP_DIR, "testDir2", fileName));
		assertThat(FileUtil.toString(file2)).isEqualTo("haha\nhehe");

		File dir3 = new File(FilePathUtil.contact(Platforms.TMP_DIR, "testDir3"));
		FileUtil.moveDir(dir, dir3);
		File file3 = new File(FilePathUtil.contact(Platforms.TMP_DIR, "testDir3", fileName));
		assertThat(FileUtil.toString(file3)).isEqualTo("haha\nhehe");
		assertThat(FileUtil.isDirExists(dir)).isFalse();

	}

	@Test
	public void fileExist() throws IOException {
		assertThat(FileUtil.isDirExists(Platforms.TMP_DIR)).isTrue();
		assertThat(FileUtil.isDirExists(Platforms.TMP_DIR + RandomUtil.nextInt())).isFalse();

		File tmpFile = null;
		try {
			tmpFile = FileUtil.createTempFile().toFile();
			assertThat(FileUtil.isFileExists(tmpFile)).isTrue();

			assertThat(FileUtil.isFileExists(tmpFile.getAbsolutePath() + RandomUtil.nextInt())).isFalse();

		} finally {
			FileUtil.deleteFile(tmpFile);
		}
	}

	@Test
	public void getName() {

		assertThat(FileUtil.getFileName(FilePathUtil.normalizePath("/a/d/b/abc.txt"))).isEqualTo("abc.txt");
		assertThat(FileUtil.getFileName("abc.txt")).isEqualTo("abc.txt");

		assertThat(FileUtil.getFileExtension(FilePathUtil.normalizePath("a/d/b/abc.txt"))).isEqualTo("txt");
		assertThat(FileUtil.getFileExtension(FilePathUtil.normalizePath("/a/d/b/abc"))).isEqualTo("");
		assertThat(FileUtil.getFileExtension(FilePathUtil.normalizePath("/a/d/b/abc."))).isEqualTo("");

	}

}
