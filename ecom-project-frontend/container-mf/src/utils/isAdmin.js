function isAdmin() {
  const isAdminValue = localStorage.getItem("isAdmin") || false;
  return isAdminValue;
}

module.exports = isAdmin;
