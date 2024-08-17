export function formatDate(date) {
  const options = { day: "numeric", month: "short" };
  return new Intl.DateTimeFormat("en-IN", options).format(date);
}
