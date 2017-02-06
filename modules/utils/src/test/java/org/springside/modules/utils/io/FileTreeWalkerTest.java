package org.springside.modules.utils.io;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springside.modules.utils.number.RandomUtil;

public class FileTreeWalkerTest {

	@Test
	public void listFile() throws IOException {
		File tmpDir = FileUtil.createTempDir();

		List<File> all = FileTreeWalker.listAll(tmpDir);
		assertThat(all).hasSize(1);

		List<File> files = FileTreeWalker.listFile(tmpDir);
		assertThat(files).hasSize(0);

		FileUtil.touch(FilePathUtil.contact(tmpDir.getAbsolutePath(), "tmp-" + RandomUtil.nextInt()) + ".tmp");
		FileUtil.touch(FilePathUtil.contact(tmpDir.getAbsolutePath(), "tmp-" + RandomUtil.nextInt()) + ".abc");

		String childDir = FilePathUtil.contact(tmpDir.getAbsolutePath(), "tmp-" + RandomUtil.nextInt());
		FileUtil.makesureDirExists(childDir);

		FileUtil.touch(FilePathUtil.contact(childDir, "tmp-" + RandomUtil.nextInt()) + ".tmp");

		all = FileTreeWalker.listAll(tmpDir);
		assertThat(all).hasSize(5);

		files = FileTreeWalker.listFile(tmpDir);
		assertThat(files).hasSize(3);

		//extension
		files = FileTreeWalker.listFileWithExtension(tmpDir, "tmp");
		assertThat(files).hasSize(2);

		files = FileTreeWalker.listFileWithExtension(tmpDir, "tp");
		assertThat(files).hasSize(0);

		//wildcard
		files = FileTreeWalker.listFileWithWildcardFileName(tmpDir, "*.tmp");
		assertThat(files).hasSize(2);
		files = FileTreeWalker.listFileWithWildcardFileName(tmpDir, "*.tp");
		assertThat(files).hasSize(0);

		//regex
		files = FileTreeWalker.listFileWithRegexFileName(tmpDir, ".*\\.tmp");
		assertThat(files).hasSize(2);
		files = FileTreeWalker.listFileWithRegexFileName(tmpDir, ".*\\.tp");
		assertThat(files).hasSize(0);
		
		
		//antpath
		files = FileTreeWalker.listFileWithAntPath(tmpDir, "**/*.tmp");
		assertThat(files).hasSize(2);
		
		files = FileTreeWalker.listFileWithAntPath(tmpDir, "*/*.tmp");
		assertThat(files).hasSize(1);
		
		files = FileTreeWalker.listFileWithAntPath(tmpDir, "*.tp");
		assertThat(files).hasSize(0);

		FileUtil.deleteDir(tmpDir);

		assertThat(FileUtil.isDirExists(tmpDir)).isFalse();

	}

}
