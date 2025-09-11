import dayjs from 'dayjs'

/**
 * 格式化时间，支持毫秒/秒时间戳、日期字符串、Date 对象
 * @param {string | number | Date} val - 时间戳/日期字符串/Date 对象
 * @param {string} formatStr - 格式字符串，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的时间
 */
export function formatDate(val, formatStr = 'YYYY-MM-DD HH:mm:ss') {
  if (!val) return '-'

  let timestamp

  if (val instanceof Date) {
    timestamp = val
  } else if (typeof val === 'number') {
    // 如果是秒级时间戳（10位数字），转换成毫秒
    timestamp = val < 1e12 ? val * 1000 : val
  } else if (typeof val === 'string') {
    const num = Number(val)
    if (!isNaN(num)) {
      timestamp = num < 1e12 ? num * 1000 : num
    } else {
      // 尝试解析日期字符串
      timestamp = new Date(val)
    }
  } else {
    return '-'
  }

  return dayjs(timestamp).format(formatStr)
}

