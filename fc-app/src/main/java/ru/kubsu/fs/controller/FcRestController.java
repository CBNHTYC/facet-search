package ru.kubsu.fs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.fs.dto.details.DetailsDto;
import ru.kubsu.fs.dto.query.ParametrizedQuery;
import ru.kubsu.fs.dto.query.RangeParameter;
import ru.kubsu.fs.dto.response.PhonesResponse;
import ru.kubsu.fs.dto.user.UserDto;
import ru.kubsu.fs.entity.Detail;
import ru.kubsu.fs.entity.ElastModel;
import ru.kubsu.fs.entity.User;
import ru.kubsu.fs.entity.View;
import ru.kubsu.fs.model.DetailDictionary;
import ru.kubsu.fs.model.DetailsEnum;
import ru.kubsu.fs.model.PhoneInfo;
import ru.kubsu.fs.repository.ElastDao;
import ru.kubsu.fs.repository.FcDao;
import ru.kubsu.fs.utils.DetailsMapper;
import ru.kubsu.fs.utils.TransferQueryResultTypeTransformer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/fc/rest", produces = MediaType.APPLICATION_JSON_VALUE)
public class FcRestController {

    private final FcDao fcDao;
    private final TransferQueryResultTypeTransformer queryResultTypeMapper;
    private final DetailsMapper detailsMapper;
    private final ElastDao elastDao;

    @Value("${min.view.time}")
    private Integer minViewTime;
    @Value("${last.viewed.nodes.count}")
    private Integer lastNodesCount;
    @Value("${aw.prise.nodes.count}")
    private Integer awPriceNodesCount;
    @Value("${max.recomm.response.size}")
    private Integer maxRequestSize;
    @Value("${price.range}")
    private Integer priceRange;

    private final String PHONES = "phones";

    @Autowired
    public FcRestController(FcDao fcDao, TransferQueryResultTypeTransformer queryResultTypeMapper,
                            DetailsMapper detailsMapper, ElastDao elastDao) {
        this.fcDao = fcDao;
        this.queryResultTypeMapper = queryResultTypeMapper;
        this.detailsMapper = detailsMapper;
        this.elastDao = elastDao;
    }

    @PostMapping(path = "getPhones", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPhones(@RequestBody String queryJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        ParametrizedQuery query = objectMapper.readValue(queryJson, ParametrizedQuery.class);
        List<ElastModel> modelList = elastDao.getPhonesByParameters(query);
        if (modelList.size() > 0) {
            PhonesResponse phonesResponse = queryResultTypeMapper.transform(modelList);
            objectMapper.writeValue(sw, phonesResponse);
        } else {
            modelList = elastDao.getAdditPhonesByParameters(query);
            PhonesResponse phonesResponse = queryResultTypeMapper.transformAddit(modelList);
            objectMapper.writeValue(sw, phonesResponse);
        }
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }

    @GetMapping(path = "getRecommendedPhoneList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRecommendedPhoneList(@RequestParam("userId") String userId) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        HashSet<ElastModel> modelSet = new HashSet<>();
        if (StringUtils.hasText(userId)) {
            User user = fcDao.getUserById(Long.valueOf(userId));
            if (Optional.ofNullable(user).isPresent()) {
                List<View> viewListFiltered = user.getViewList()
                        .stream()
                        .filter(view -> view.getTime() >= minViewTime)
                        .sorted(Comparator.comparingInt(View::getTime))
                        .collect(Collectors.toList());
                if (viewListFiltered.size() > lastNodesCount) {
                    viewListFiltered = viewListFiltered.subList(0, lastNodesCount - 1);
                }
                List<Long> idList = viewListFiltered
                        .stream()
                        .map(view -> view.getModel().getModelId())
                        .collect(Collectors.toList());
                modelSet.addAll(elastDao.getPhoneListByIdList(idList));

                if (Optional.ofNullable(user.getAwPrice()).isPresent()) {
                    ParametrizedQuery parametrizedQuery = new ParametrizedQuery();
                    RangeParameter rangeParameter = new RangeParameter();
                    if (user.getAwPrice() > 0) {
                        Integer minPrice = user.getAwPrice() - priceRange;
                        Integer maxPrice = user.getAwPrice() + priceRange;
                        rangeParameter.setValueBegin(String.valueOf(minPrice >= 0 ? minPrice : 0));
                        rangeParameter.setValueEnd(String.valueOf(maxPrice));
                        rangeParameter.setName("price");
                        parametrizedQuery.setRangeParameter(Collections.singletonList(rangeParameter));
                        List<ElastModel> paramModelList = elastDao.getPhonesByParameters(parametrizedQuery)
                                .stream()
                                .sorted(Comparator.comparingInt(ElastModel::getViews))
                                .collect(Collectors.toList());
                        if (paramModelList.size() > awPriceNodesCount) {
                            paramModelList = paramModelList.subList(0, awPriceNodesCount - 1);
                        }
                        modelSet.addAll(paramModelList);
                    }
                }
                if (modelSet.size() < maxRequestSize) {
                    modelSet.addAll(elastDao.getMostViewedPhones().subList(0, maxRequestSize - modelSet.size() - 1));
                }
            } else {
                modelSet.addAll(elastDao.getMostViewedPhones());
            }
        } else {
            modelSet.addAll(elastDao.getMostViewedPhones());
        }
        List<ElastModel> elastModelList = new ArrayList<>(modelSet);
        if (elastModelList.size() > maxRequestSize) {
            elastModelList = elastModelList.subList(0, maxRequestSize - 1);
        }
        PhonesResponse phonesResponse = queryResultTypeMapper.transform(elastModelList);
        objectMapper.writeValue(sw, phonesResponse);
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }

    @GetMapping(path = "phone", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPhoneById(@RequestParam("id") String id, @RequestParam("userId") String userId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            StringWriter sw = new StringWriter();
            PhoneInfo phoneInfo = elastDao.getPhoneById(id);
            PhonesResponse phonesResponse = queryResultTypeMapper.transform(phoneInfo.getPhone(),
                    phoneInfo.getAccessories());
            objectMapper.writeValue(sw, phonesResponse);

            if (StringUtils.hasText(userId)) {
                User user = fcDao.getUserById(Long.valueOf(userId));
                if (Optional.ofNullable(user).isPresent()) {
                    if (user.getViewList().stream()
                            .noneMatch(view -> view.getModel().getModelId() ==
                                    Long.parseLong(phoneInfo.getPhone().getModelId()))) {
                        View view = new View();
                        view.setUser(user);
                        view.setModel(fcDao.getModelById(Long.parseLong(phoneInfo.getPhone().getModelId())));
                        view.setTime(0);
                        fcDao.saveView(view);
                        Integer sumPrice = 0;
                        for (View view1 : user.getViewList()) {
                            sumPrice += view1.getModel().getPrice();
                        }
                        sumPrice += phoneInfo.getPhone().getPrice();
                        Integer size = user.getViewList().size() + 1;
                        user.setAwPrice(sumPrice / size);
                        fcDao.saveUser(user);
                    }
                }
            }
            return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
        } catch (NotFoundException nfe) {
            return new ResponseEntity<>("Телефон не найден", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("Ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "accessory", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAccessoryById(@RequestParam("id") String id) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            StringWriter sw = new StringWriter();
            ElastModel elastModel = elastDao.getAccessoryById(id);
            PhonesResponse phonesResponse = queryResultTypeMapper.transform(elastModel, Collections.emptyList());
            objectMapper.writeValue(sw, phonesResponse);
            return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
        } catch (NotFoundException nfe) {
            return new ResponseEntity<>("Аксессуар не найден", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("Ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "getDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDetails(@RequestParam("category") String category) throws IOException {
        switch (category) {
            case PHONES:
                ObjectMapper objectMapper = new ObjectMapper();
                DetailsDto detailsDto = new DetailsDto();
                List<Detail> detailList = fcDao.getAllDetails();
                detailsDto.setDetailDtoList(detailsMapper.map(detailList));
                detailsDto.getDetailDtoList().forEach(detailDto -> {
                    DetailDictionary detailDictionary = Arrays.stream(DetailsEnum.values())
                            .filter(detailsEnum -> detailsEnum.getValue().getRu().equals(detailDto.getName()))
                            .findAny().get().getValue();
                    try {
                        detailDto.setElastDetailValueList(elastDao.getDetaiValuesByDetailName(detailDictionary.getEn()));
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                });
                StringWriter sw = new StringWriter();
                objectMapper.writeValue(sw, detailsDto);
                return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
            default:
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "pulse")
    public ResponseEntity<String> pulse(@RequestParam("userId") String userId, @RequestParam("modelId") String modelId, @RequestParam("time") Integer time)
            throws IOException {
        if (StringUtils.hasText(userId)) {
            User user = fcDao.getUserById(Long.valueOf(userId));
            List<ElastModel> elastModel = elastDao.getPhoneListByIdList(Collections.singletonList(Long.valueOf(modelId)));
            if (Optional.ofNullable(user).isPresent()) {
                if (elastModel.size() > 0) {
                    Optional<View> optView = user.getViewList().stream()
                            .filter(view -> view.getModel().getModelId() == Long.parseLong(elastModel.get(0).getModelId()))
                            .findAny();
                    if (optView.isPresent()) {
                        View view = optView.get();
                        view.setTime(view.getTime() + Optional.ofNullable(time).orElse(0));
                        fcDao.saveView(view);
                    }
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path = "login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@RequestParam("email") String email, @RequestParam("password") String password)
            throws IOException {
        if (StringUtils.hasText(email) && StringUtils.hasText(password)) {
            User user = fcDao.getUserByEmail(email);
            if (Optional.ofNullable(user).isPresent()) {
                if (password.equals(user.getPassword())) {
                    StringWriter sw = new StringWriter();
                    ObjectMapper objectMapper = new ObjectMapper();
                    UserDto userDto = new DozerBeanMapper().map(user, UserDto.class);
                    objectMapper.writeValue(sw, userDto);
                    return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
                } else
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } else {
                join(email, password);
                return login(email, password);
            }
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(path = "join", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> join(@RequestParam("email") String email, @RequestParam("password") String password) {
        if (StringUtils.hasText(email) && StringUtils.hasText(password)) {
            User user = fcDao.getUserByEmail(email);
            if (!Optional.ofNullable(user).isPresent()) {
                user = new User();
                user.setAwPrice(0);
                user.setEmail(email);
                user.setPassword(password);
                fcDao.saveUser(user);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> fulTextSearch(@RequestParam("query") String query) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        List<ElastModel> modelList = elastDao.fullTextSearch(query);
        PhonesResponse phonesResponse = queryResultTypeMapper.transform(modelList);
        objectMapper.writeValue(sw, phonesResponse);
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }
}
