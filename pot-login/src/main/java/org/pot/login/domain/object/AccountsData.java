package org.pot.login.domain.object;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "account")
@Getter
@Setter
public class AccountsData {
	@Id
	private long id;
	private long account_id;
	private long openid;
	private long created_at;
	private long updated_at;
	private long status;
}
