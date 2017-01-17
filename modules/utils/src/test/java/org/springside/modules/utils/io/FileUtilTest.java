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
		File file = FileUtil.createTempFile("abc", ".tmp");
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
		File newFile = new File(FilePathUtil.contact(Platforms.TMP_DIR, "testFile" + RandomUtil.nextInt()));

		FileUtil.copyFile(file, newFile);
		assertThat(FileUtil.isFileExists(newFile)).isTrue();

		String result = FileUtil.toString(newFile);
		assertThat(result).isEqualTo(content);

	}

	@Test
	public void fileExist() throws IOException {
		assertThat(FileUtil.isDirExists(Platforms.TMP_DIR)).isTrue();
		assertThat(FileUtil.isDirExists(Platforms.TMP_DIR + RandomUtil.nextInt())).isFalse();

		File tmpFile = null;
		try {
			tmpFile = FileUtil.createTempFile();
			assertThat(FileUtil.isFileExists(tmpFile)).isTrue();

			assertThat(FileUtil.isFileExists(tmpFile.getAbsolutePath() + RandomUtil.nextInt())).isFalse();

		} finally {
			FileUtil.deleteFile(tmpFile);
		}
	}

	@Test
	public void listFile() throws IOException {
		File tmpDir = FileUtil.createTempDir();

		List<File> all = FileUtil.listAll(tmpDir);
		assertThat(all).hasSize(1);

		List<File> files = FileUtil.listFile(tmpDir);
		assertThat(files).hasSize(0);

		FileUtil.touch(FilePathUtil.contact(tmpDir.getAbsolutePath(), "tmp-" + RandomUtil.nextInt()) + ".tmp");
		FileUtil.touch(FilePathUtil.contact(tmpDir.getAbsolutePath(), "tmp-" + RandomUtil.nextInt()) + ".abc");

		String childDir = FilePathUtil.contact(tmpDir.getAbsolutePath(), "tmp-" + RandomUtil.nextInt());
		FileUtil.makeSureDirExists(childDir);

		FileUtil.touch(FilePathUtil.contact(childDir, "tmp-" + RandomUtil.nextInt()) + ".tmp");

		all = FileUtil.listAll(tmpDir);
		assertThat(all).hasSize(5);

		files = FileUtil.listFile(tmpDir);
		assertThat(files).hasSize(3);

		files = FileUtil.listFileWithExtension(tmpDir, "tmp");
		assertThat(files).hasSize(2);

		FileUtil.deleteDir(tmpDir);

		assertThat(FileUtil.isDirExists(tmpDir)).isFalse();

	}

}
