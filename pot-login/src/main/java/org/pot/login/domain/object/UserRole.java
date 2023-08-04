package org.pot.login.domain.object;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "account")
@Getter
@Setter
public class UserRole implements Serializable {
	@Id
	private long id;
	private long account_id;
	private long openid;
	private long created_at;
	private long updated_at;
	private long status;
}
