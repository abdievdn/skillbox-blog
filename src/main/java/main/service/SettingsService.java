package main.service;

import main.api.request.SettingsRequest;
import main.api.response.SettingsResponse;
import main.model.GlobalSettings;
import main.model.repository.SettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    private final SettingsRepository settingsRepository;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
        checkData();
    }

    private void checkData() {
        Iterable<GlobalSettings> globalSettings = settingsRepository.findAll();
        if (globalSettings.iterator().hasNext()) {
            return;
        }
        insertSettings(SettingsCode.MULTIUSER_MODE.name(), "Многопользовательский режим", "YES");
        insertSettings(SettingsCode.POST_PREMODERATION.name(), "Премодерация постов", "YES");
        insertSettings(SettingsCode.STATISTICS_IS_PUBLIC.name(), "Показывать всем статистику блога", "YES");
    }

    private void insertSettings(String code, String name, String value) {
        GlobalSettings globalSettings = new GlobalSettings();
        globalSettings.setCode(code);
        globalSettings.setName(name);
        globalSettings.setValue(value);
        settingsRepository.save(globalSettings);
    }

    public SettingsResponse getGlobalSettings() {
        SettingsResponse settingsResponse = new SettingsResponse();
        Iterable<GlobalSettings> globalSettings = settingsRepository.findAll();
        for (GlobalSettings settings : globalSettings) {
            switch (SettingsCode.valueOf(settings.getCode())) {
                case MULTIUSER_MODE :
                    settingsResponse.setMultiuserMode(stringToBoolean(settings.getValue()));
                    break;
                case POST_PREMODERATION:
                    settingsResponse.setPostPremoderation(stringToBoolean(settings.getValue()));
                    break;
                case STATISTICS_IS_PUBLIC:
                    settingsResponse.setStatisticsIsPublic(stringToBoolean(settings.getValue()));
                    break;
                default:
                    break;
            }
        }
        return settingsResponse;
    }

    public void setGlobalSettings(SettingsRequest settingsRequest) {
        Iterable<GlobalSettings> globalSettingsIterable = settingsRepository.findAll();
        for (GlobalSettings settings : globalSettingsIterable) {
            switch (SettingsCode.valueOf(settings.getCode())) {
                case MULTIUSER_MODE:
                    settings.setValue(booleanToString(settingsRequest.isMultiuserMode()));
                    break;
                case POST_PREMODERATION:
                    settings.setValue(booleanToString(settingsRequest.isPostPremoderation()));
                    break;
                case STATISTICS_IS_PUBLIC:
                    settings.setValue(booleanToString(settingsRequest.isStatisticsIsPublic()));
                    break;
                default: break;
            }
            settingsRepository.save(settings);
        }
    }

    private boolean stringToBoolean(String value) {
        return value.equals("YES");
    }

    private String booleanToString (boolean value) {
        return value == true ? "YES" : "NO";
    }
}