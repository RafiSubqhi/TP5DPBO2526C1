-- Database: db_people

CREATE DATABASE db_people;
USE db_people;

CREATE TABLE person (
    id varchar(255) PRIMARY KEY,
    nama varchar(255) NOT NULL,
    tahun_lahir int NOT NULL,
    kategori varchar(255) NOT NULL,
    wfh boolean NOT NULL
);

-- Memasukkan data awal yang sesuai dengan method populateList() milikmu
-- beserta beberapa data tambahan acak dengan kategori yang sesuai dengan ComboBox
INSERT INTO person (id, nama, tahun_lahir, kategori, wfh) VALUES
('EMP001', 'Shi Hao', 2005, 'Developer', true),
('EMP002', 'Ye Fan', 2004, 'DevOps', false),
('EMP003', 'Chu Feng', 1988, 'Data Analyst', true),
('EMP004', 'Tang San', 1995, 'UI/UX Designer', false),
('EMP005', 'Xiao Yan', 2002, 'QA Tester', true),
('EMP006', 'Wang Lin', 2003, 'Developer', false),
('EMP007', 'Luo Feng', 1992, 'UI/UX Designer', true);