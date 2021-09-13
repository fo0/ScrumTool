package com.fo0.vaadin.scrumtool.ui.export;

import com.fo0.vaadin.scrumtool.ui.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.google.gson.Gson;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author fo0
 * @created 13.09.21 - 00:49
 */
public class ExportJson {

  private static final KBDataRepository repository = SpringContext.getBean(KBDataRepository.class);

  private static final Gson gson = SpringContext.getBean(Gson.class);

  public static String exportAsJson(String id) {
    TKBData data = repository.findByIdFetched(id);
    return gson.toJson(data);
  }

  public static boolean boardExists(String json) {
    TKBData data = gson.fromJson(json, TKBData.class);
    if (!repository.existsById(data.getId())) {
      return false;
    }

    Notification.show("Board already exists",
                      (int) TimeUnit.SECONDS.toMillis(5),
                      Position.MIDDLE);
    return true;
  }

  /**
   * @param json
   * @return id ob the board
   */
  public static String importAsJson(String json, boolean newId) {
    if (boardExists(json)) {
      return null;
    }

    TKBData data = gson.fromJson(json, TKBData.class);
    if(newId){
      data.setId(UUID.randomUUID().toString());
    }

    Notification.show("Importing Board with ID: " + data.getId(),
                      (int) TimeUnit.SECONDS.toMillis(5),
                      Position.MIDDLE);

    repository.save(data);
    return data.getId();
  }

}
