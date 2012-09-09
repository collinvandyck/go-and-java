package main

import (
	"database/sql"
	"time"
)

type User struct {
	Id        string    `json:"id"`
	Email     string    `json:"email,omitempty"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
	Name      string    `json:"name,omitempty"`
	Admin     bool      `json:"admin"`
	Active    bool      `json:"-"`
}

func GetUserByApiKey(db *sql.DB, key Credentials) (*User, error) {
	rows, err := db.Query("select id, email, created_at, updated_at, name, admin, active from users where apikey = $1", string(key))
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	if rows.Next() {
		user := new(User)
		var id []byte
		var email []byte
		var name []byte
		err = rows.Scan(&id, &email, &user.CreatedAt, &user.UpdatedAt, &name, &user.Admin, &user.Active)
		if err != nil {
			return nil, err
		}
		user.Id = string(id)
		user.Email = string(email)
		user.Name = string(name)
		return user, nil
	}
	return nil, nil
}
