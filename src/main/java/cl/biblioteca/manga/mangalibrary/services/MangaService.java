package cl.biblioteca.manga.mangalibrary.services;

import cl.biblioteca.manga.mangalibrary.models.MangaDataModel;

import java.util.List;

public interface MangaService {

    //method to load all mangas to display in the library
    List<MangaDataModel> getAllMangas();

    // method to load all mangas
//    List<MangaDataModel> getAllMangas();

    // method to load a manga chapter
    byte[] getChapter(String mangaName, String chapter);

    // method to get the chapters list
    List<String> getAllChapterNames(String mangaName);

    // method to load the info of a manga, mangaName required.
    MangaDataModel getManga(String mangaName);

}
