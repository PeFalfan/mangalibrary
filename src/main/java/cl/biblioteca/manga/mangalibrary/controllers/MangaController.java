package cl.biblioteca.manga.mangalibrary.controllers;

import cl.biblioteca.manga.mangalibrary.models.MangaDataModel;
import cl.biblioteca.manga.mangalibrary.services.MangaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mangas")
public class MangaController {

    private final MangaService mangaService;

    public MangaController(MangaService mangaService) {
        this.mangaService = mangaService;
    }

    @GetMapping("/getChapter/{mangaName}/{chapterName}")
    public ResponseEntity<byte[]> getChapter(
            @PathVariable String mangaName,
            @PathVariable String chapterName
    ) {
        byte[] data = mangaService.getChapter(mangaName, chapterName);
        return ResponseEntity.ok()
                       .contentType(MediaType.APPLICATION_OCTET_STREAM)
                       .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + chapterName + "\"")
                       .body(data);
    }

    @GetMapping("/getMangas")
    public ResponseEntity<List<MangaDataModel>> getAllMangas() {

        return ResponseEntity.ok()
                       .contentType(MediaType.APPLICATION_JSON)
                       .body(mangaService.getAllMangas());

    }

    @GetMapping("/getManga/{mangaName}")
    public ResponseEntity<MangaDataModel> getManga(
            @PathVariable String mangaName
    ) {
        return ResponseEntity.ok()
                       .contentType(MediaType.APPLICATION_JSON)
                       .body(mangaService.getManga(mangaName));
    }

    @GetMapping("/getChapters/{mangaName}")
    public ResponseEntity<List<String>> getAllChapterNames(
            @PathVariable String mangaName
    ) {
        return ResponseEntity.ok()
                       .contentType(MediaType.APPLICATION_JSON)
                       .body(mangaService.getAllChapterNames(mangaName));
    }
}
