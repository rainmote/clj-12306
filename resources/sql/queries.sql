-- :name add-tag! :<!
-- :doc add a tag
INSERT INTO tags (name) VALUES (:name) returning id, name

-- :name delete-tag! :! :n
-- :doc delete a tag
DELETE FROM tags WHERE id = :id

-- :name get-all-tags :? :*
-- :doc retrieves all tags
SELECT * FROM tags

-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(id, first_name, last_name, email, pass)
VALUES (:id, :first_name, :last_name, :email, :pass)

-- :name update-user! :! :n
-- :doc updates an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieves a user record given the id
SELECT * FROM users
WHERE id = :id

-- :name delete-user! :! :n
-- :doc deletes a user record given the id
DELETE FROM users
WHERE id = :id
