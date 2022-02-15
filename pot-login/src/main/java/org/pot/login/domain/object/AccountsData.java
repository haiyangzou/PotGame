package org.pot.login.domain.object;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "account")
@Getter
@Setter
public class AccountsData implements Serializable {
	@Id
	private long id;
	private long account_id;
	private long openid;
	private long created_at;
	private long updated_at;
	private long status;
}
