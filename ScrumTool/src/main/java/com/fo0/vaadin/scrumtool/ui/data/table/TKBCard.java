package com.fo0.vaadin.scrumtool.ui.data.table;

import com.fo0.vaadin.scrumtool.ui.data.enums.ECardType;
import com.fo0.vaadin.scrumtool.ui.data.interfaces.IDataOrder;
import com.fo0.vaadin.scrumtool.ui.model.TextItem;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TKBCard implements Serializable, IDataOrder {

  private static final long serialVersionUID = 652620276690725942L;

  @Id
  @Builder.Default
  private String id = UUID.randomUUID()
                          .toString();

  private String ownerId;

  @Builder.Default
  private int dataOrder = -1;

  @Builder.Default
  private ECardType type = ECardType.TextCard;

  @Lob
  private String text;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "cardId")
  @Builder.Default
  private Set<TKBCardComment> comments = Sets.newHashSet();

  public int getLikes() {
    return getByType(TextItem.class)
        .orElse(TextItem.builder()
                        .build())
        .countAllLikes();
  }

  public void setTextByType(Object o) {
    setText(new Gson().toJson(o));
  }

  public <T> Optional<T> getByType(Class<T> type) {
    T data = null;
    try {
      data = new Gson().fromJson(text, type);
    } catch (Exception e) {
    }

    return Optional.ofNullable(data);
  }

  public <T> Optional<List<T>> getByTypeAsList(Class<T> type) {
    List<T> data = null;
    try {
      data = new Gson().fromJson(text,
                                 TypeToken.getParameterized(ArrayList.class, type)
                                          .getType());
    } catch (Exception e) {
    }

    return Optional.ofNullable(data);
  }

}
