package blossom.project.towelove.loves.service.Impl;

import blossom.project.towelove.common.exception.ServiceException;
import blossom.project.towelove.common.page.PageResponse;
import blossom.project.towelove.common.request.loves.diary.*;
import blossom.project.towelove.common.response.Result;
import blossom.project.towelove.common.response.love.diary.*;
import blossom.project.towelove.framework.mysql.config.JacksonTypeHandler;
import blossom.project.towelove.framework.user.core.UserInfoContextHolder;
import blossom.project.towelove.loves.convert.DiaryCollectionConvert;
import blossom.project.towelove.loves.convert.DiaryConvert;
import blossom.project.towelove.loves.entity.LoveDiary;
import blossom.project.towelove.loves.entity.LoveDiaryCollection;
import blossom.project.towelove.loves.entity.LoveDiaryImage;
import blossom.project.towelove.loves.mapper.DiariesMapper;
import blossom.project.towelove.loves.mapper.DiaryMapper;
import blossom.project.towelove.loves.mapper.LoveDiaryImageMapper;
import blossom.project.towelove.loves.mapper.LoveDiaryMapper;
import blossom.project.towelove.loves.service.DiariesService;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @projectName: Towelove
 * @package: blossom.project.towelove.loves.service.Impl
 * @className: DiariesServiceImpl
 * @author: Link Ji
 * @description: GOGO
 * @date: 2024/2/20 15:54
 * @version: 1.0
 */
@Service
@RequiredArgsConstructor
public class DiariesServiceImpl extends ServiceImpl<DiariesMapper, LoveDiaryCollection> implements DiariesService {

    private final DiariesMapper diariesMapper;

    private final LoveDiaryMapper loveDiaryMapper;


    private final LoveDiaryImageMapper diaryImageMapper;

    private final Logger log = LoggerFactory.getLogger(DiariesService.class);


    public final String QUICK_WRITE_DIARY_TITLE = "小记一下";
    public final String QUICK_WRITE_DIARY_COVER = "https://oss.towelove.cn/towelove-images/2024/03/12/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20240308182109_20240312132621A044.jpg";

//    private final DiaryService diaryService;

    @Override
    public List<DiaryCollectionDTO> getDiaryCollectionById() {
        Long userId = UserInfoContextHolder.getUserId();
        LambdaQueryWrapper<LoveDiaryCollection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(userId), LoveDiaryCollection::getUserId, userId);
        queryWrapper.orderByAsc(LoveDiaryCollection::getCreateTime);
        List<LoveDiaryCollection> loveDiaryCollections = diariesMapper.selectList(queryWrapper);
        return DiaryCollectionConvert.INSTANCE.convert(loveDiaryCollections);
    }

    @Override
    public PageResponse<DiaryCollectionDTO> getDiaryCollectionByPage(DiaryCollectionPageRequest request) {
        Long userId = UserInfoContextHolder.getUserId();
        LambdaQueryWrapper<LoveDiaryCollection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(userId), LoveDiaryCollection::getUserId, userId);
        queryWrapper.orderByAsc(LoveDiaryCollection::getCreateBy);
        Page<LoveDiaryCollection> page = new Page<>(request.getPageNo() - 1, request.getPageSize());
        page = diariesMapper.selectPage(page, queryWrapper);
        List<DiaryCollectionDTO> result = DiaryCollectionConvert.INSTANCE.convert(page.getRecords());
        return new PageResponse<>(page.getCurrent() //页码
                , page.getSize()
                , page.getTotal()//数量
                , result); //结果集
    }

    @Override
    public DiaryCollectionDTO createDiaryCollection(DiaryCollectionCreateRequest request) {
        if (StrUtil.isBlank(request.getTitle())) {
            request.setTitle(DateUtil.format(Date.from(Instant.now()), "yyyy-MM-dd HH:mm:ss"));
        }
        LoveDiaryCollection loveDiaryCollection = DiaryCollectionConvert.INSTANCE.convert(request);
        if (Objects.isNull(loveDiaryCollection)) {
            throw new ServiceException("入参有误");
        }
        Long userId = UserInfoContextHolder.getUserId();
        Long coupleId = UserInfoContextHolder.getCoupleId();
        loveDiaryCollection.setUserId(userId);
        loveDiaryCollection.setCoupleId(coupleId);
        try {
            diariesMapper.insert(loveDiaryCollection);
        } catch (Exception e) {
            throw new ServiceException("创建日记失败");
        }
        return DiaryCollectionConvert.INSTANCE.convert(loveDiaryCollection);
    }

    @Override
    public Boolean deleteById(Long id) {
        int loop;
        try {
            loop = diariesMapper.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("删除日机册失败");
        }
        return loop > 0;
    }

    @Override
    public List<DiaryTitleDTO> getLoveDirayByCollectionId(Long collectionId) {
        List<DiaryTitleDTO> diaryByCollectionDtos = null;
        try {
            diaryByCollectionDtos = loveDiaryMapper.getDiaryByCollectionId(collectionId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceException("查询日记结果失败");
        }
        return diaryByCollectionDtos;
    }

    /**
     * 创建日记
     *
     * @param request
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public LoveDiaryDTO createDiary(DiaryCreateRequest request) {
        //先查询日记册是否存在
        LambdaQueryWrapper<LoveDiaryCollection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoveDiaryCollection::getId, request.getDiaryCollectionId());
        if (!diariesMapper.exists(queryWrapper)) {
            throw new ServiceException("非法请求！日记册不存在");
        }
        LoveDiary entity = DiaryConvert.INSTANCE.convert(request);
        try {
            loveDiaryMapper.insert(entity);
            //获取图片集合
            if (!request.getImages().isEmpty()) {
                List<LoveDiaryImage> loveDiaryImages = request.getImages().stream().map((image) -> LoveDiaryImage.builder()
                        .diaryId(entity.getId())
                        .url(image.getUrl())
                        .build()).toList();
                diaryImageMapper.insertBatch(loveDiaryImages);
            }
        } catch (Exception e) {
            log.error(String.format("日记册：%s创建日记失败,失败原因为：%s", request.getDiaryCollectionId(), e.getMessage()));
            throw new ServiceException("创建日记失败");
        }

        LoveDiaryDTO result = DiaryConvert.INSTANCE.convert(entity);
        result.setImages(request.getImages());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean fetchSynchronous(Long id, Boolean synchronous) {
        Long coupleId = UserInfoContextHolder.getCoupleId();
        if (Objects.isNull(coupleId)) {
            throw new ServiceException("没有情侣关系");
        }
        //判断是否两人已经拥有同步日记册
        LambdaQueryWrapper<LoveDiaryCollection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoveDiaryCollection::getCoupleId, coupleId);
        queryWrapper.eq(LoveDiaryCollection::getStatus, 1);
        if (!diariesMapper.exists(queryWrapper)) {
            //不存在，则创建一条
            LoveDiaryCollection collection = LoveDiaryCollection.builder()
                    .userId(null)
                    .coupleId(coupleId)
                    .title("我们的日记")
                    .cover(QUICK_WRITE_DIARY_COVER)
                    .build();
            collection.setStatus(1);
            try {
                diariesMapper.insert(collection);
            } catch (Exception e) {
                throw new ServiceException("新建同步日记册失败");
            }
        }
        LoveDiary entity = LoveDiary.builder()
                .id(id)
                .synchronous(synchronous)
                .build();
        try {
            loveDiaryMapper.updateById(entity);
        } catch (Exception e) {
            throw new ServiceException("更新同步状态异常");
        }
        return synchronous;
    }

    @Override
    public List<DiaryTitleDTO> getLoveDirayBySynchronous() {
        //先判断是否有情侣关系
        Long coupleId = UserInfoContextHolder.getCoupleId();
        if (Objects.isNull(coupleId)) {
            throw new ServiceException("请求非法，没有情侣关系");
        }
        return loveDiaryMapper.getDiaryBySynchronous(coupleId);
    }

    @Override
    public LoveDiaryVO getLoveDiaryById(Long id) {
        LoveDiary loveDiary = loveDiaryMapper.selectById(id);
        if (Objects.isNull(loveDiary)) {
            throw new ServiceException("请求非法，日记不存在");
        }
        List<DiaryImageDto> diaryImageDtos = diaryImageMapper.getImageUrlByDiaryId(id);
        //获取对应图片
        LoveDiaryVO loveDiaryVO = DiaryConvert.INSTANCE.convert2Vo(loveDiary);
        loveDiaryVO.setImages(diaryImageDtos);
        return loveDiaryVO;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String quickWrite(QuickWriterDiaryRequest request) {
        Long userId = UserInfoContextHolder.getUserId();
        Long coupleId = UserInfoContextHolder.getCoupleId();
        //快速创建日记
        //先判断速记日记本是否存在
        Long quickWriteCollectionId = diariesMapper.findQuickWriteCollection(userId, QUICK_WRITE_DIARY_TITLE);
        if (Objects.isNull(quickWriteCollectionId)) {
            //不存在则快速创建
            LoveDiaryCollection collection = LoveDiaryCollection.builder()
                    .userId(userId)
                    .coupleId(null)
                    .title(QUICK_WRITE_DIARY_TITLE)
                    .cover(QUICK_WRITE_DIARY_COVER)
                    .build();
            if (Objects.nonNull(collection)) {
                collection.setCoupleId(coupleId);
            }
            try {
                diariesMapper.insert(collection);
            } catch (Exception e) {
                log.info("创建日记册失败：{}", e.getMessage());
                throw new ServiceException("小记一下失败，创建日记册失败");
            }
            quickWriteCollectionId = collection.getId();
        }
        //正常记录
        LoveDiary loveDiary = LoveDiary.builder()
                .diaryCollectionId(quickWriteCollectionId)
                .title(DateUtil.format(Date.from(Instant.now()), JacksonTypeHandler.DEFAULT_DATE_FORMAT))
                .synchronous(false)
                .content(request.getContent())
                .build();
        if (loveDiaryMapper.insert(loveDiary) < 1) {
            throw new ServiceException("小记一下失败");
        }
        return request.getContent();
    }

    @Override
    public DiaryCollectionDTO getLoveDiariesBySyn() {
        Long coupleId = UserInfoContextHolder.getCoupleId();
        if (Objects.isNull(coupleId)) {
            return null;
        }
        LambdaQueryWrapper<LoveDiaryCollection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoveDiaryCollection::getCoupleId, coupleId);
        queryWrapper.eq(LoveDiaryCollection::getStatus, 1);
        List<LoveDiaryCollection> loveDiaryCollections = diariesMapper.selectList(queryWrapper);
        return loveDiaryCollections.isEmpty() ? null : DiaryCollectionConvert.INSTANCE.convert(loveDiaryCollections.get(0));
    }

    /**
     * 更新日记册
     *
     * @param updateDiaryRequest
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String updateDiary(UpdateDiaryRequest updateDiaryRequest) {
        //更新日记册
        LoveDiary updateLoveDiaryDo = DiaryConvert.INSTANCE.convert(updateDiaryRequest);
        try {
            loveDiaryMapper.updateById(updateLoveDiaryDo);
            if (Objects.nonNull(updateDiaryRequest.getImages()) && !updateDiaryRequest.getImages().isEmpty()) {
                //查询当前日记的照片集合
                List<String> imagesFromDB = diaryImageMapper.selectImageUrlByDiaryId(updateDiaryRequest.getId());
                //两个集合取差集
                List<String> imageFromRequest = updateDiaryRequest.getImages()
                        .stream()
                        .map(DiaryImageRequest::getUrl)
                        .toList();
                //得到需要插入数据库的数据
                List<LoveDiaryImage> updateImageToDB = imageFromRequest
                        .stream()
                        .filter(imagesFromDB::contains)
                        .map(ele -> LoveDiaryImage.builder()
                                .diaryId(updateDiaryRequest.getId())
                                .url(ele)
                                .build()).toList();
                if (!updateImageToDB.isEmpty()) {
                    diaryImageMapper.insertBatch(updateImageToDB);
                }
            }
        } catch (Exception e) {
            throw new ServiceException("更新日记册失败");
        }
        return Result.ok().getMsg();
    }

}
