package eki.ekimedia.service.db;

import static eki.ekimedia.data.db.Tables.MEDIA_FILE;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.ekimedia.constant.SystemConstant;
import eki.ekimedia.data.db.tables.MediaFile;

@Component
public class MediaDbService implements GlobalConstant, SystemConstant {

	@Autowired
	private DSLContext db;

	public Long createMediaFile(eki.ekimedia.data.MediaFile mediaFile) {

		return db
				.insertInto(
						MEDIA_FILE,
						MEDIA_FILE.ORIGIN,
						MEDIA_FILE.ORIGINAL_FILENAME,
						MEDIA_FILE.OBJECT_FILENAME,
						MEDIA_FILE.FILENAME_EXT)
				.values(
						mediaFile.getOrigin(),
						mediaFile.getOriginalFilename(),
						mediaFile.getObjectFilename(),
						mediaFile.getFilenameExt())
				.returning(MEDIA_FILE.ID)
				.fetchOne()
				.getId();
	}

	public boolean mediaFileExistsByObjectFilename(String objectFilename) {

		MediaFile mf = MEDIA_FILE.as("mf");

		return db
				.fetchExists(DSL
						.select(mf.ID)
						.from(mf)
						.where(mf.OBJECT_FILENAME.eq(objectFilename)));
	}

	public eki.ekimedia.data.MediaFile getMediaFileByObjectFilename(String objectFilename) {

		MediaFile mf = MEDIA_FILE.as("mf");

		return db
				.selectFrom(mf)
				.where(mf.OBJECT_FILENAME.eq(objectFilename))
				.fetchOptionalInto(eki.ekimedia.data.MediaFile.class)
				.orElse(null);
	}

	public void deleteMediaFileByObjectFilename(String objectFilename) {

		db
				.deleteFrom(MEDIA_FILE)
				.where(MEDIA_FILE.OBJECT_FILENAME.eq(objectFilename))
				.execute();
	}
}
