package kopo.poly.service.impl;

import kopo.poly.dto.*;
import kopo.poly.service.IMainService;
import kopo.poly.util.AllSetDTOUtil;
import kopo.poly.util.PasingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service("MainService")
public class MainService implements IMainService {


    @Override
    public List<No1IftDTO> getNo1Ift(String year, String dsvd) throws ParserConfigurationException, IOException, SAXException {
        log.info("start!!");
        String url = "getCallStat01Api";
        String []keyArr = {"numOfRows","pageNo", "year", "dvsd"};
        String []valueArr = {"500", "1", year, URLEncoder.encode(dsvd, "UTF-8")};

        Document document = PasingUtil.xmlPasing(url, keyArr, valueArr);
        List<No1IftDTO> no1IftList = AllSetDTOUtil.allSetNo1Ift(document);

        return no1IftList;
    }

    @Override
    public List<No4IftDTO> getNo4Ift(String year, String dsvd) throws ParserConfigurationException, IOException, SAXException {
        String url = "getCallStat04Api";
        String []keyArr = {"numOfRows","pageNo", "year", "dvsd"};
        String []valueArr = {"500", "1", year, URLEncoder.encode(dsvd, "UTF-8")};

        Document document = PasingUtil.xmlPasing(url, keyArr, valueArr);
        List<No4IftDTO> no4IftList = AllSetDTOUtil.allSetNo4Ift(document);

        return no4IftList;
    }

    @Override
    public List<HospitalDTO> getHospital() throws IOException, ParserConfigurationException, SAXException {
        log.info(this.getClass().getName() + " getHospital Start !!");
        String url = "getpubReliefHospList";
        String []keyArr = {"numOfRows","pageNo"};
        String []valueArr = {"500", "1"};

        List<HospitalDTO> hospitalList = new ArrayList<>();

        Document document = PasingUtil.xmlPasing(url, keyArr, valueArr);
        document.getDocumentElement().normalize();
        NodeList nList = document.getElementsByTagName("item");

        for(int temp=0; temp<nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            Element eElement = (Element) nNode;
            HospitalDTO hospitalDTO = new HospitalDTO();

            hospitalDTO.setType(PasingUtil.getTagValue("spclAdmTyCd", eElement));
            hospitalDTO.setName(PasingUtil.getTagValue("yadmNm", eElement));
            hospitalDTO.setLocation(PasingUtil.getTagValue("sidoNm", eElement));
            hospitalDTO.setPhoneNumber(PasingUtil.getTagValue("telno", eElement));
            hospitalDTO.setDetailLocation(PasingUtil.getTagValue("sgguNm", eElement));

            hospitalList.add(hospitalDTO);
        }

        log.info(this.getClass().getName() + " getHospital End !!");
        return hospitalList;
    }

    @Override
    public List<BogunsoDTO> getBogunso() throws Exception {
        log.info(this.getClass().getName() + " getBogunso Start !!");

        File f = new File("/usr/local/csvFile/bogunso3.csv");
        FileReader fileReader = new FileReader(f);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        List<BogunsoDTO> bogunsoList = new ArrayList<>();

        String line = "";
        while((line = bufferedReader.readLine()) != null) {
            String [] splitLine = line.split(",");
            BogunsoDTO bogunsoDTO = new BogunsoDTO();
            bogunsoDTO.setLocationDetail(splitLine[0]);
            bogunsoDTO.setName(splitLine[1]);
            bogunsoDTO.setLocation(splitLine[2].substring(0,4));
            bogunsoDTO.setAddr(splitLine[2]);
            bogunsoDTO.setPhoneNumber(splitLine[3]);
            bogunsoDTO.setType(splitLine[5]);
            bogunsoDTO.setTime(splitLine[6]);
            bogunsoList.add(bogunsoDTO);
        }
        bufferedReader.close();

        log.info(this.getClass().getName() + " getBogunso End !!");
        return bogunsoList;
    }

    @Override
    public List<CovidPaDTO> getCovidPa() throws ParserConfigurationException, IOException, SAXException {
        log.info(this.getClass().getName() + " .getCovidPa Start !!");
        String url = "getCallCovid04Api";
        String []keyArr = {"numOfRows","pageNo","std_day"};
        String []valueArr = {"500", "1", "2022-12-20"};

        Document document = PasingUtil.xmlPasing(url, keyArr, valueArr);

        List<CovidPaDTO> covidPaList = AllSetDTOUtil.allSetCovidPa(document);
        log.info(this.getClass().getName() + " .getCovidPa End !!");
        return covidPaList;
    }

    @Override
    public String getCovidNum() throws ParserConfigurationException, IOException, SAXException {
        log.info(this.getClass().getName() + ".getCovidNum Start !!");
        String url = "getCovid19CurrentStatusConfirmationsJson";
        String []keyArr = {"numOfRows","pageNo"};
        String []valueArr = {"100", "1"};

        String todayCovidNum = "";
        try {
            JSONObject jsonObject = PasingUtil.jsonPasing(url, keyArr, valueArr);
            jsonObject =(JSONObject)jsonObject.get("response");
            JSONArray jsonArray =(JSONArray)jsonObject.get("result");
            jsonObject = (JSONObject) jsonArray.get(0);
            todayCovidNum = jsonObject.get("cnt7").toString();
        } catch (NullPointerException e) {
            todayCovidNum = "0";
        }
        log.info(this.getClass().getName() + ".getCovidNum End !!");
        return todayCovidNum;
    }
}
