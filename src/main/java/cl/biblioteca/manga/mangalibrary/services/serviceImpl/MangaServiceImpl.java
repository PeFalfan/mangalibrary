package cl.biblioteca.manga.mangalibrary.services.serviceImpl;

import cl.biblioteca.manga.mangalibrary.models.MangaDataModel;
import cl.biblioteca.manga.mangalibrary.services.MangaService;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class MangaServiceImpl implements MangaService {


    private static final Logger logger = LoggerFactory.getLogger(MangaServiceImpl.class);

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public MangaServiceImpl(
            @Value("${minio.url}") String url,
            @Value("${minio.accessKey}") String accessKey,
            @Value("${minio.secretKey}")String secretKey
    ) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Override
    public byte[] getChapter(String mangaName, String chapter) {
        logger.info("Loading chapter {}, of the manga {}", chapter, mangaName);
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucket).object(mangaName+"/"+chapter).build())) {
            logger.info("Chapter {} loaded", chapter);
            return stream.readAllBytes();
        } catch (Exception e) {
            logger.error("Error retrieving chapter", e);
            throw new RuntimeException("Error retrieving chapter", e);
        }
    }

    @Override
    public List<String> getAllChapterNames(String mangaName) {
        List<String> chapters = new ArrayList<>();

        try {
            Iterable<Result<Item>> results = getAllChapters(mangaName);

            for (Result<Item> result : results) {
                chapters.add(result.get().objectName());
            }



            return chapters;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public MangaDataModel getManga(String mangaName) {

        try {

            getAllChapters(mangaName);
            Iterable<Result<Item>> results = getAllChapters(mangaName);

            MangaDataModel manga = new MangaDataModel();

            manga.setId(1L);
            manga.setTitle(mangaName);
            int chapterCount = 0;
            for (Result<Item> result : results) {
                chapterCount++;
            }
            manga.setCurrentChapters(chapterCount);
            manga.setMainTag("Manga");
            manga.setDescription("description TODO");
            manga.setCoverUrl("https://i.pinimg.com/736x/fa/b3/c7/fab3c7fc01d8262e7361bdf7f699213e.jpg");
            manga.setYearOfRelease(2019);

            return manga;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<MangaDataModel> getAllMangas() {
        // load from minIo:
        Set<String> names = getAllMangasNames();
        List<MangaDataModel> mangas = new ArrayList<>();

        // create the MangaDataModelObjects
        for (String name : names) {
            MangaDataModel manga = new MangaDataModel();

            manga.setId(1L);
            manga.setTitle(name);
            manga.setCurrentChapters(1);
            manga.setMainTag("tag");
            manga.setDescription("description");
            manga.setCoverUrl("https://i.pinimg.com/736x/fa/b3/c7/fab3c7fc01d8262e7361bdf7f699213e.jpg");
            manga.setYearOfRelease(2021);

            mangas.add(manga);
        }

        return mangas;
    }

    //load all chapters in a series
    public Iterable<Result<Item>> getAllChapters(String mangaName){
        try {
            return minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket)
                            .prefix(mangaName+"/")
                            .recursive(true)
                            .build()
            );
        } catch (Exception e) {
            logger.error("Error retrieving chapters", e);
            throw new RuntimeException("Error retrieving chapters", e);
        }
    }

    //load from minIO
    public Set<String> getAllMangasNames(){
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket)
                            .recursive(true)
                            .build()
            );

            List<String> names = new ArrayList<>();

            for (Result<Item> result : results) {

                names.add(result.get().objectName());
            }

            Set<String> filteredNames = new LinkedHashSet<>();

            for (String name : names) {
                String[] splittedName = name.split("/");

                if (!names.contains(splittedName[0])) {
                    filteredNames.add(splittedName[0]);
                }
            }

            return filteredNames;

        } catch (Exception e) {
            logger.error("Error retrieving mangas names", e);
            throw new RuntimeException("Error retrieving mangas names", e);
        }
    }

}
