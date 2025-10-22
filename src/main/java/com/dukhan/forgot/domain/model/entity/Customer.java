package com.dukhan.forgot.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rbx_t_user_auth")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auth_id")
	private Long authId;

	@Column(name = "auth_type", length = 30)
	private String authType;

	@Column(name = "channel_id", length = 50)
	private String channelId;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "created_by", length = 100)
	private String createdBy;

	@Column(name = "customer_id")
	private Long customerId;

	@Column(name = "date_migrated")
	private LocalDateTime dateMigrated;

	@Column(name = "failed_login_attempts")
	private Integer failedLoginAttempts;

	@Column(name = "force_pwd_change", length = 1)
	private String forcePwdChange;

	@Column(name = "force_user_change", length = 1)
	private String forceUserChange;

	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;

	@Column(name = "last_login_channel", length = 50)
	private String lastLoginChannel;

	@Column(name = "last_login_ip", length = 100)
	private String lastLoginIp;

	@Column(name = "last_unsuccessful_login")
	private LocalDateTime lastUnsuccessfulLogin;

	@Column(name = "password_changed_at", nullable = false)
	private LocalDateTime passwordChangedAt;

	@Column(name = "password_hash", length = 255, nullable = false)
	private String passwordHash;

	@Lob
	@Column(name = "pwd_cipher_base64")
	private byte[] pwdCipherBase64;

	@Lob
	@Column(name = "pwd_enc_key_base64")
	private byte[] pwdEncKeyBase64;

	@Column(name = "pwd_iv_base64", length = 64)
	private String pwdIvBase64;

	@Column(name = "server_salt", length = 64, nullable = false)
	private String serverSalt;

	@Column(name = "status", length = 20)
	private String status;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "user_id", length = 50, nullable = false, unique = true)
	private String userId;

	@Column(name = "user_type", length = 30)
	private String userType;

	@Column(name = "user_no", nullable = false)
	private Long userNo;

	@Column(name = "updated_by", length = 50)
	private String updatedBy;
}