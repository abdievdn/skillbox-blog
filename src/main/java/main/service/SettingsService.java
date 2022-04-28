package main.service;

import main.api.response.SettingsResponse;
import main.model.GlobalSettings;
import main.model.repository.SettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    public static final String MULTIUSER_MODE = "MULTIUSER_MODE";
    public static final String POST_PREMODERATION = "POST_PREMODERATION";
    public static final String STATISTICS_IS_PUBLIC = "STATISTICS_IS_PUBLIC";
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
        insertSettings(MULTIUSER_MODE, "Многопользовательский режим", "NO");
        insertSettings(POST_PREMODERATION, "Премодерация постов", "YES");
        insertSettings(STATISTICS_IS_PUBLIC, "Показывать всем статистику блога", "YES");
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
            switch (settings.getCode()) {
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

    private boolean stringToBoolean(String value) {
        return value.equals("YES");
    }
}