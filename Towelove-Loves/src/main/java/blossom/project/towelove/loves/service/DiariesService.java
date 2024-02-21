package blossom.project.towelove.loves.service;

import blossom.project.towelove.common.page.PageResponse;
import blossom.project.towelove.common.request.loves.diary.DiaryCollectionCreateRequest;
import blossom.project.towelove.common.request.loves.diary.DiaryCollectionPageRequest;
import blossom.project.towelove.common.request.loves.diary.DiaryCreateRequest;
import blossom.project.towelove.common.response.Result;
import blossom.project.towelove.common.response.love.diary.DiaryCollectionDTO;
import blossom.project.towelove.common.response.love.diary.DiaryTitleDTO;
import blossom.project.towelove.common.response.love.diary.LoveDiaryDTO;
import blossom.project.towelove.loves.entity.LoveDiaryCollection;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @projectName: Towelove
 * @package: blossom.project.towelove.loves.service
 * @className: DiariesService
 * @author: Link Ji
 * @description: GOGO
 * @date: 2024/2/20 15:53
 * @version: 1.0
 */
public interface DiariesService extends IService<LoveDiaryCollection>{
    List<DiaryCollectionDTO> getDiaryCollectionById(Long coupleId);

    PageResponse<DiaryCollectionDTO> getDiaryCollectionByPage(DiaryCollectionPageRequest request);

    DiaryCollectionDTO createDiaryCollection(DiaryCollectionCreateRequest request);


    Boolean deleteById(Long id);

    List<DiaryTitleDTO> getLoveDirayByCollectionId(Long collectionId);

    LoveDiaryDTO createDiary(DiaryCreateRequest request);

    Boolean fetchSynchronous(Long id,Boolean synchronous);
}
