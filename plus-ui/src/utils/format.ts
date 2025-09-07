import dayjs from 'dayjs'

/**
 * 格式化时间
 * @param {string | number | Date} val - 时间戳/日期字符串/Date 对象
 * @param {string} formatStr - 格式字符串，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的时间
 */
export function formatDate(val, formatStr = 'YYYY-MM-DD HH:mm:ss') {
  if (!val) return '-'
  return dayjs(val).format(formatStr)
}
