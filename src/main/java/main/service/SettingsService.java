package main.service;

import main.Blog;
import main.api.request.general.SettingsRequest;
import main.api.response.general.SettingsResponse;
import main.model.GlobalSettings;
import main.model.repository.SettingsRepository;
import main.service.enums.SettingsCode;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    private final SettingsRepository settingsRepository;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
        createSettingsIfNoExist();
    }

    public SettingsResponse getGlobalSettings() {
        SettingsResponse settingsResponse = new SettingsResponse();
        Iterable<GlobalSettings> settings = settingsRepository.findAll();
        settings.forEach(s -> {
            if (isMultiuserMode(s)) {
                settingsResponse.setMultiuserMode(stringToBoolean(s.getValue()));
            }
            if (isPostPremoderation(s)) {
                settingsResponse.setPostPremoderation(stringToBoolean(s.getValue()));
            }
            if (isStatisticsIsPublic(s)) {
                settingsResponse.setStatisticsIsPublic(stringToBoolean(s.getValue()));
            }
        });
        return settingsResponse;
    }

    public void setGlobalSettings(SettingsRequest settingsRequest) {
        Iterable<GlobalSettings> settings = settingsRepository.findAll();
        settings.forEach(s -> {
            if (isMultiuserMode(s)) {
                s.setValue(booleanToString(settingsRequest.isMultiuserMode()));
            }
            if (isPostPremoderation(s)) {
                s.setValue(booleanToString(settingsRequest.isPostPremoderation()));
            }
            if (isStatisticsIsPublic(s)) {
                s.setValue(booleanToString(settingsRequest.isStatisticsIsPublic()));
            }
            settingsRepository.save(s);
        });
    }

    private boolean isMultiuserMode(GlobalSettings s) {
        return s.getCode().equals(SettingsCode.MULTIUSER_MODE.name());
    }

    private boolean isPostPremoderation(GlobalSettings s) {
        return s.getCode().equals(SettingsCode.POST_PREMODERATION.name());
    }

    private boolean isStatisticsIsPublic(GlobalSettings s) {
        return s.getCode().equals(SettingsCode.STATISTICS_IS_PUBLIC.name());
    }

    private void createSettingsIfNoExist() {
        Iterable<GlobalSettings> globalSettings = settingsRepository.findAll();
        if (globalSettings.iterator().hasNext()) {
            return;
        }
        insertSetting(SettingsCode.MULTIUSER_MODE.name(), Blog.SETTINGS_MULTIUSER_MODE_TEXT, Blog.YES_VALUE);
        insertSetting(SettingsCode.POST_PREMODERATION.name(), Blog.SETTINGS_POST_PREMODERATION_TEXT, Blog.YES_VALUE);
        insertSetting(SettingsCode.STATISTICS_IS_PUBLIC.name(), Blog.SETTINGS_STATISTICS_IS_PUBLIC_TEXT, Blog.NO_VALUE);
    }

    private void insertSetting(String code, String name, String value) {
        GlobalSettings globalSettings = new GlobalSettings();
        globalSettings.setCode(code);
        globalSettings.setName(name);
        globalSettings.setValue(value);
        settingsRepository.save(globalSettings);
    }

    private boolean stringToBoolean(String value) {
        return value.equals("YES");
    }

    private String booleanToString(boolean value) {
        return value ? "YES" : "NO";
    }
}