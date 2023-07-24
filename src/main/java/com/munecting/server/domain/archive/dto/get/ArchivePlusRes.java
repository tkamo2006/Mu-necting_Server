package com.munecting.server.domain.archive.dto.get;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ArchivePlusRes {
    private String musicName;
    private String nick;
    private LocalDateTime createAt; //저장 날짜
    private String coverImg;
    private String writing;
    public ArchivePlusRes(String musicName,String nick,LocalDateTime createAt,String coverImg,String writing){
        this.musicName = musicName;
        this.nick = nick;
        this.createAt = createAt;
        this.coverImg = coverImg;
        this.writing = writing;
    }
}
