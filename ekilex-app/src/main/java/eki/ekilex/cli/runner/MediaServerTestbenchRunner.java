package eki.ekilex.cli.runner;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.data.MediaFileContent;
import eki.common.data.MediaFileRef;
import eki.ekilex.client.EkimediaClient;
import eki.ekilex.constant.SystemConstant;

//TODO remove after media server full integration
@Component
public class MediaServerTestbenchRunner implements GlobalConstant, SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(MediaServerTestbenchRunner.class);

	@Autowired
	private EkimediaClient ekimediaClient;

	public void execute() throws Exception {

		//createMediaFile();
		//deleteMediaFile();
	}

	private void createMediaFile() throws Exception {

		logger.info("Uploading media file...");

		//File mediaFile = new File("/projects/ekilex/aws/ruhnu-lennuk.jpg");
		File mediaFile = new File("/projects/ekilex/aws/jäätee.jpg");
		String filename = mediaFile.getName();
		byte[] content = FileUtils.readFileToByteArray(mediaFile);

		MediaFileContent mediaFileContent = new MediaFileContent();
		mediaFileContent.setFilename(filename);
		mediaFileContent.setContent(content);

		MediaFileRef mediaFileRef = ekimediaClient.createMediaFile(mediaFileContent);

		logger.info("...done");

		System.out.println(mediaFileRef.getUrl());
		System.out.println(mediaFileRef);
	}

	private void deleteMediaFile() throws Exception {

		String objectFilename = "b7cddf88d957467e952adde0318a50d4.jpg";
		ekimediaClient.deleteMediaFile(objectFilename);
	}
}
