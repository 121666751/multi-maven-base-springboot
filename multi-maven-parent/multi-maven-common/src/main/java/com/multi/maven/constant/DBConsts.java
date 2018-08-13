package com.multi.maven.constant;

/**
 * @Author: litao
 * @Description: 数据库常量
 * @Date: 11:39 2018/8/9
 */
public interface DBConsts {

    /**
     * 系统操作者-默认为0L
     */
    public static final Long SYS_OPT = 0L;

    /**
     * DB标准字段-主键
     */
    public static final String FIELD_ID = "id";

    /**
     * DB标准字段-用户主键
     */
    public static final String FIELD_UID = "uid";

    /**
     * DB标准字段-状态
     */
    public static final String FIELD_STATUS = "status";

    /**
     * DB标准字段-逻辑状态
     */
    public static final String FIELD_DR = "enabled";

    /**
     * DB标准字段-时间戳
     */
    public static final String FIELD_TS = "update_time";

    /**
     * 数据逻辑状态 - 正常态
     */
    public static final Byte DR_NORMAL = 0;

    /**
     * 数据逻辑状态 - 删除态
     */
    public static final Byte DR_DELETE = 1;

    /**
     * 数据逻辑状态 - 已提審
     */
    public static final Byte DR_AUDITING = 2;
    /**
     * 数据逻辑状态 - 已審核
     */
    public static final Byte DR_AUDITED = 3;

    /**
     * 启用标识 - 启用
     */
    public static final Byte STATUS_ENABLE = 0;

    /**
     * 启用标识 - 停用
     */
    public static final Byte STATUS_DISABLE = 1;

    /**
     * 用户认证标识- 已认证
     */
    public static final Byte AUTH_NORMAL = 1;
    /**
     * 用户认证标识- 未认证
     */
    public static final Byte UNCERTIFIED = 0;


     /**
      * 是否在首页展示 0:是  1：否
      */
    public static final Byte IS_HOME_YES = 0;
    public static final Byte IS_HOME_NO = 1;

    /**
     * 参数为空
     */
    public static final int PARAM_NULL = 100402;

    /**
     * 返回数据为空
     */
    public static final int RESULT_EMPTY = 100500;

    /**
     * 更新失败
     */
    public static final int OPER_UPDATE_FAIL = 100303;

    /**
     * 0=>待激活 1=>待使用 2=>占用中 3=>已使用 4=>已失效
     */
    public static final int COUPON_VATION = 1;

    /**
     * DB标准字段-类型
     */
    public static final String FIELD_TYPE = "type";

    /**
     * DB默认分页
     */
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_SIZE_THREE = 3;
    /**
     * 房源lbsid置为0 （上下线、删除房源等)
     */
    public static final int HOUSE_LBSID_ZERO = 0;


    /**
     * '0：下线   1：上线',
     */
    public static final Byte ONLINE = 1;
    public static final Byte OFFLINE = 0;

    /**
     * '0：非默认账户   1：默认账户',
     */
    public static final int IS_DEFAULT_NO = 0;

    /**
     * '0：非默认账户   1：默认账户',
     */
    public static final int IS_DEFAULT_YES = 1;

    /**
     * 分享返利  优惠券
     * '0：没使用   1：使用',
     */
    public static final Byte IS_USE_NO = 0;

    /**
     * 分享返利  优惠券
     * '0：没使用   1：使用',
     */
    public static final Byte IS_USE_YES = 1;
    public static final String HOUSE_IS_ONLINE="online";
    /**
     * 注册成功标识
     */
    public static final Byte REG_RESULT_SUCCESS=Byte.valueOf("0");
    /**
     * 注册失败标识
     */
    public static final Byte REG_RESULT_FAIL=Byte.valueOf("1");

    /**
     * 评论对象的类型：0.房源，1.活动 2 酒店
     */
    public static final Byte OBJ_TYPE_HOTEL = 2;

    /**
     * 酒店图片类型 :1=>外观 2=>设施 3=>卧室  4=>客厅
     */
    public static final Byte HOTEL_IMG_TYPE = 1;


    /**
     * 是否为首图 0=>否 1=>是
     */
    public static final Byte IS_COVER_IMG = 1;

    /**
     * 0=>暂停售卖,1=>正常售卖,3=>已删除
     */
    public static final Byte HOTEL_HOUSE_STATUS_NORMAL = 1;
    /**
     * 活动状态 开启
     */
    public static final Byte DATE_PRICE_STATUS_OPEN=1;
    /**
     * 活动状态 暂停
     */
    public static final Byte DATE_PRICE_STATUS_STOP=0;
    /**
     * 关注表角色默认
     */
    public static final Byte FOLLOW_ROLE_DEFAULT=0;
    /**
     * 关注表角色房东
     */
    public static final Byte FOLLOW_ROLE_LANGLORD=1;
    /**
     * 关注表角色达人
     */
    public static final Byte FOLLOW_ROLE_EXPERT=2;


    /**
     * 0=>限时取消,1=>不可取消,2=>随时取消
     */
    public static final Byte REFUND_TYPE_NO = 1;

    /**
     * 0=>限时取消,1=>不可取消,2=>随时取消
     */
    public static final Byte REFUND_TYPE_YES = 2;

    /**
     * 旅行订单需要商家确认
     */
    public static final Byte TRAVEL_ORDER_CONFIRM_TRUE=1;
    /**
     * 旅行订单无需商家确认
     */
    public static final Byte TRAVEL_ORDER_CONFIRM_FALSE=0;

    /**
     * 房东收入
     */
    public final static String ORDER_INCOME_OBJ_CODE_LANDLORD="1";

/**
 *酒店订单状态
 *全部：0
    待确认 1
    待支付 2
    已取消 3
    已过期 4
    已拒绝 5
    待入住 6
    入住中 7
    已完成 8*/
 public static final Byte HOTEL_ORDER_PENDING_PAYMENT = 2;

 public static final Byte HOTEL_ORDER_EXPIRED = 4;


 /**
  * 待支付
  */
 public static final String UNPAY= "0";

 /**
  * 已支付
  */
 public static final String  PAYED = "11";

 /**
  * 待入住
  */
 public static final String  UNCHECKIN= "12";

 /**
  * 已入住
  */
 public static final String  CHECKINED = "13";

 /**
  * 已离店
  */
 public static final String  LEAVED = "14";

 /**
  * 已点评
  */
 public static final String  COMMENTED = "15";

 /**
  * 已取消
  */
 public static final String  CANCELED = "21";

 /**
  * 退款中
  */
 public static final String  REFOUNDING = "31";

 /**
  * 已退款
  */
 public static final String  REFOUNDED = "32";

 /**
  * 退款失败
  */
 public static final String  REFOUND_FAILED = "33";

   /**
    * 添加酒店评论，更新solr数据
    */
 public static final String  SYN_SORL_URL = "http://localhost:8088";

  /**
   * 更新房源solr数据
   */
 public static final String  SYN_SORL_HOUSE_URL = "http://localhost:8086";


  /**
   *  阿里云消息推送
   */
 public final static String HOTEL_ORDER_QUEUE = "order.hotel";

 public final static String ORDER_QUEUE = "139OrderQueue";

 public final static String HOUSE_QUEUE = "house.solr.sync";

 public final static String TRAVEL_ORDER_QUEUE = "139TravelOrderQueue";

 public final static String APP_PUSH_QUEUE = "139AppPushQueue";



 public static final String  LINK_TYPE_H5= "1";
 public static final String  LINK_TYPE_HOUSE= "5";
 public static final String  LINK_TYPE_HOTEL= "6";
 public static final String  LINK_TYPE_TRAVEL= "7";

 /**
  * 优惠券使用记录  参考coupon_use 的type字段   0=>民宿 1=>当地活动 2=>主题higo 3=>酒店
  */
 public static final Integer COUPON_USE_TYPE_HOUSE = 0;

 //中国代码
 public final static Integer CHINA_NA = 10001;

 //番茄来了
 public static final Byte UP_TYPE_FANQIE = 4;

 //同城旅游
 public static final Byte UP_TYPE_TONGCHENG = 5;
 public static final String PAGE_NUM="pageNum";
 public static final String PAGE_SIZE="pageSize";

 //民宿APP短信发送MD5校验内容
 public static final String MD5_CHECK = "0000";

}
