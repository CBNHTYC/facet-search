package ru.kubsu.fs.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.fs.entity.*;

import java.util.List;

@Service
public class FcDao {

    private final DetailsRepository detailsRepository;
    private final ModelRepository modelRepository;
    private final ImageRepository imageRepository;
    private final VendorRepository vendorRepository;
    private final UserRepositiry userRepositiry;
    private final ViewRepository viewRepository;

    @Autowired
    public FcDao(DetailsRepository detailsRepository, ModelRepository modelRepository, @Qualifier("imageRepository") ImageRepository imageRepository, VendorRepository vendorRepository, UserRepositiry userRepositiry, ViewRepository viewRepository) {
        this.detailsRepository = detailsRepository;
        this.modelRepository = modelRepository;
        this.imageRepository = imageRepository;
        this.vendorRepository = vendorRepository;
        this.userRepositiry = userRepositiry;
        this.viewRepository = viewRepository;
    }

//    @Transactional
//    public List<Model> getModelList(TransferQueryParametersType body) {
//        TransferQueryParametersType.QueryParameters queryParameters = body.getQueryParameters();
//        StringBuilder query = new StringBuilder();
//        String paramValue;
//        SimpleParameterType simpleParameterTypePrevious = new SimpleParameterType();
//        RangeParameterType rangeParameterTypePrevious = new RangeParameterType();
//        int simpleSize = queryParameters.getSimpleParameter().size();
//        int rangeSize = queryParameters.getRangeParameter().size();
//
//        if(simpleSize > 0) {
//            for (SimpleParameterType simpleParameterType : queryParameters.getSimpleParameter()) {
//                if (queryParameters.getSimpleParameter().indexOf(simpleParameterType) != 0) {
//                    if (simpleParameterType.getName().equals(simpleParameterTypePrevious.getName())) {
//                        query.append(" OR ");
//                    } else {
//                        query.append(") AND (");
//                    }
//                }else {
//                    query.append("(");
//                }
//
//                if(simpleParameterType.getType().equals("String")) {
//                    paramValue = "'" + simpleParameterType.getValue() + "'";
//                }else {
//                    paramValue = simpleParameterType.getValue();
//                }
//                query.append(simpleParameterType.getName()).append("=").append(paramValue);
//
//                if (queryParameters.getSimpleParameter().indexOf(simpleParameterType) == simpleSize - 1) {
//                    query.append(")");
//                }
//                simpleParameterTypePrevious = simpleParameterType;
//            }
//        }
//
//        if(rangeSize > 0) {
//            if(simpleSize > 0){
//                query.append(" AND ");
//            }
//            for (RangeParameterType rangeParameterType : queryParameters.getRangeParameter()){
//                if (queryParameters.getRangeParameter().indexOf(rangeParameterType) != 0) {
//                    if (rangeParameterType.getName().equals(rangeParameterTypePrevious.getName())) {
//                        query.append(" OR ");
//                    } else {
//                        query.append(") AND (");
//                    }
//                }else {
//                    query.append("(");
//                }
//                query.append(rangeParameterType.getName()).append(" BETWEEN ").append(rangeParameterType.getValueBegin()).append(" AND ").append(rangeParameterType.getValueEnd());
//
//                if (queryParameters.getRangeParameter().indexOf(rangeParameterType) == rangeSize - 1) {
//                    query.append(")");
//                }
//                rangeParameterTypePrevious = rangeParameterType;
//            }
//        }
//        return jdbcPhoneRepositoryImpl.getModels(query.toString());
//    }

    @Transactional
    public List<Model> getAllModels() {
        return modelRepository.findAll();
    }

    @Transactional
    public Model getModelById(Long id) { return modelRepository.findByModelId(id); }

    @Transactional
    public List<Detail> getAllDetails() {
        return detailsRepository.findAll();
    }

    @Transactional
    public List<Vendor> getAllVendors() { return vendorRepository.findAll(); }

    @Transactional
    public List<Image> getImagesByModelId(Long modelId) {
        return imageRepository.findAllByModelModelId(modelId);
    }

    @Transactional
    public List<User> getAllUsers() {return userRepositiry.findAll(); }

    @Transactional
    public User getUserById(Long userId) { return userRepositiry.findUserByUserId(userId); }

    @Transactional
    public User getUserByEmail(String email) { return userRepositiry.findUserByEmail(email); }

    @Transactional
    public void saveUser(User user) { userRepositiry.save(user); }

    @Transactional
    public void saveView(View view) { viewRepository.save(view); }
}
